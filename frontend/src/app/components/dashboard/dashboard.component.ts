import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { BankAccount } from '../../models/banking.models';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  accounts: BankAccount[] = [];
  loading: boolean = true;
  errorMessage: string = '';

  constructor(
    private accountService: AccountService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.errorMessage = '';

    this.accountService.getMyAccounts()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (res) => {
          this.accounts = res.data || [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Dashboard Load Error:', err);
          this.errorMessage = err.error?.message || 'Failed to load accounts.';
          this.cdr.detectChanges();
        }
      });
  }

  get totalBalance(): number {
    return this.accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0);
  }
}