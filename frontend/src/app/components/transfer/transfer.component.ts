import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { BeneficiaryService } from '../../services/beneficiary.service';
import { BankAccount, Beneficiary } from '../../models/banking.models';
import { finalize, debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css'],
  standalone: false
})
export class TransferComponent implements OnInit {
  transferForm: FormGroup;
  beneficiaryForm: FormGroup;

  myAccounts: BankAccount[] = [];
  myBeneficiaries: Beneficiary[] = [];

  showAddBeneficiaryModal: boolean = false;
  recipientName: string = '';
  recipientLoading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';
  loading: boolean = false;
  beneficiaryLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private beneficiaryService: BeneficiaryService
  ) {
    this.transferForm = this.fb.group({
      sourceAccountNumber: ['', Validators.required],
      targetAccountNumber: ['', [Validators.required, Validators.minLength(10)]],
      amount: ['', [Validators.required, Validators.min(1)]],
      description: ['']
    });

    this.beneficiaryForm = this.fb.group({
      name: ['', Validators.required],
      accountNumber: ['', [Validators.required, Validators.minLength(10)]],
      bankName: ['AetherBank', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadAccounts();
    this.loadBeneficiaries();

    // Auto-Lookup Recipient Name on Typing
    this.transferForm.get('targetAccountNumber')?.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(accNum => {
      if (accNum && accNum.trim().length >= 10) {
        this.lookupRecipient(accNum.trim());
      } else {
        this.recipientName = '';
      }
    });
  }

  loadAccounts(): void {
    this.accountService.getMyAccounts().subscribe(res => {
      this.myAccounts = res.data || [];
      if (this.myAccounts.length > 0) {
        this.transferForm.patchValue({ sourceAccountNumber: this.myAccounts[0].accountNumber });
      }
    });
  }

  loadBeneficiaries(): void {
    this.beneficiaryService.getMyBeneficiaries().subscribe({
      next: (res) => this.myBeneficiaries = res.data || [],
      error: (err) => console.error('Failed to load beneficiaries:', err)
    });
  }

  onSelectBeneficiary(event: any): void {
    const selectedAcc = event.target.value;
    if (selectedAcc) {
      this.transferForm.patchValue({ targetAccountNumber: selectedAcc });
      this.lookupRecipient(selectedAcc);
    }
  }

  toggleBeneficiaryModal(): void {
    this.showAddBeneficiaryModal = !this.showAddBeneficiaryModal;
  }

  onAddBeneficiarySubmit(): void {
    if (this.beneficiaryForm.invalid) return;

    this.beneficiaryLoading = true;
    this.beneficiaryService.addBeneficiary(this.beneficiaryForm.value)
      .pipe(finalize(() => this.beneficiaryLoading = false))
      .subscribe({
        next: (res) => {
          this.loadBeneficiaries();
          this.transferForm.patchValue({ targetAccountNumber: res.data.accountNumber });
          this.lookupRecipient(res.data.accountNumber);
          this.beneficiaryForm.reset({ bankName: 'AetherBank' });
          this.showAddBeneficiaryModal = false;
        },
        error: (err) => {
          alert(err.error?.message || 'Failed to save beneficiary.');
        }
      });
  }

  lookupRecipient(accountNumber: string): void {
    this.recipientLoading = true;
    this.accountService.getAccountByNumber(accountNumber).subscribe({
      next: (res) => {
        this.recipientLoading = false;
        if (res.data && res.data.ownerName) {
          this.recipientName = res.data.ownerName;
        } else {
          this.recipientName = 'Valid Account';
        }
      },
      error: () => {
        this.recipientLoading = false;
        this.recipientName = '';
      }
    });
  }

  onSubmit(): void {
    if (this.transferForm.invalid) return;

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.transactionService.transfer(this.transferForm.value)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (res) => {
          this.successMessage = `Transfer of ₹${res.data.amount} successful! Ref: ${res.data.transactionReference}`;
          this.transferForm.reset();
          this.recipientName = '';
          if (this.myAccounts.length > 0) {
            this.transferForm.patchValue({ sourceAccountNumber: this.myAccounts[0].accountNumber });
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.message || err.message || 'Transfer failed. Please check balance and account details.';
        }
      });
  }
}