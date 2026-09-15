import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { BankAccount } from '../../models/banking.models';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css'],
  standalone: false
})
export class AccountsComponent implements OnInit {
  accountForm: FormGroup;
  accounts: BankAccount[] = [];
  successMessage: string = '';
  errorMessage: string = '';
  loading: boolean = false;

  // Deposit Inline State
  activeDepositAccount: string | null = null;
  depositAmount: number = 5000;
  depositLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef
  ) {
    this.accountForm = this.fb.group({
      accountType: ['SAVINGS', Validators.required],
      initialDeposit: [1000.00, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.accountService.getMyAccounts().subscribe({
      next: (res) => {
        this.accounts = res.data || [];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Load Accounts Error:', err)
    });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) return;

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const payload = {
      accountType: this.accountForm.value.accountType,
      initialDeposit: Number(this.accountForm.value.initialDeposit)
    };

    this.accountService.createAccount(payload)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (res) => {
          this.successMessage = `Bank Account ${res.data.accountNumber} created successfully!`;
          this.loadAccounts();
        },
        error: (err) => {
          console.error('Create Account Error:', err);
          this.errorMessage = err.error?.message || err.message || 'Failed to open account.';
          this.cdr.detectChanges();
        }
      });
  }

  toggleAddMoney(accountNumber: string): void {
    if (this.activeDepositAccount === accountNumber) {
      this.activeDepositAccount = null;
    } else {
      this.activeDepositAccount = accountNumber;
      this.depositAmount = 5000;
    }
  }

  confirmDeposit(accountNumber: string): void {
    if (this.depositAmount <= 0) return;

    this.depositLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.transactionService.deposit({
      accountNumber: accountNumber,
      amount: this.depositAmount,
      description: 'Instant self-service deposit'
    }).pipe(finalize(() => {
      this.depositLoading = false;
      this.activeDepositAccount = null;
      this.cdr.detectChanges();
    })).subscribe({
      next: (res) => {
        this.successMessage = `₹${this.depositAmount} deposited successfully into ${accountNumber}!`;
        this.loadAccounts();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to deposit money.';
      }
    });
  }

  deleteAccount(acc: BankAccount): void {
    const confirmClose = confirm(`Are you sure you want to permanently close bank account ${acc.accountNumber}?`);
    if (!confirmClose) return;

    this.successMessage = '';
    this.errorMessage = '';

    this.accountService.deleteAccount(acc.accountNumber).subscribe({
      next: () => {
        this.successMessage = `Bank Account ${acc.accountNumber} closed successfully.`;
        this.loadAccounts();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to close account.';
      }
    });
  }
}