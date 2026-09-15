import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { BankAccount, UserProfile, AuditLog, PagedResponse } from '../../models/banking.models';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: false
})
export class AdminDashboardComponent implements OnInit {
  users: UserProfile[] = [];
  accounts: BankAccount[] = [];
  pagedAuditLogs?: PagedResponse<AuditLog>;

  loading: boolean = true;
  actionLoading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  activeTab: 'ACCOUNTS' | 'USERS' | 'AUDIT' = 'ACCOUNTS';
  searchTerm: string = '';

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAdminData();
  }

  loadAdminData(): void {
    this.loading = true;
    this.adminService.getAllAccounts()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (res) => this.accounts = res.data || [],
        error: (err) => this.errorMessage = 'Failed to load system accounts.'
      });

    this.adminService.getAllUsers().subscribe({
      next: (res) => this.users = res.data || []
    });

    this.adminService.getAuditLogs().subscribe({
      next: (res) => this.pagedAuditLogs = res.data
    });
  }

  getCustomerName(acc: BankAccount): string {
    if (acc.ownerName && acc.ownerName !== 'Unknown' && acc.ownerName !== 'Registered Customer') {
      return acc.ownerName;
    }
    if (acc.userEmail && this.users.length > 0) {
      const matchedUser = this.users.find(u => u.email.toLowerCase() === acc.userEmail?.toLowerCase());
      if (matchedUser) {
        return `${matchedUser.firstName} ${matchedUser.lastName}`;
      }
    }
    return 'Bank Customer';
  }

  get totalLiquidity(): number {
    return this.accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0);
  }

  get filteredAccounts(): BankAccount[] {
    if (!this.searchTerm.trim()) return this.accounts;
    const term = this.searchTerm.toLowerCase();
    return this.accounts.filter(acc => 
      acc.accountNumber.toLowerCase().includes(term) ||
      (acc.ownerName && acc.ownerName.toLowerCase().includes(term)) ||
      (acc.userEmail && acc.userEmail.toLowerCase().includes(term))
    );
  }

  toggleBlockAccount(acc: BankAccount): void {
    const isBlocking = acc.status === 'ACTIVE';
    const actionText = isBlocking ? 'freeze/block' : 'unblock';
    
    const reason = prompt(`Enter administrative reason to ${actionText} account ${acc.accountNumber}:`, 
      isBlocking ? 'Security Risk / Regulatory Freeze' : 'Administrative clearance granted');
    
    if (reason === null) return;

    this.actionLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const apiCall = isBlocking 
      ? this.adminService.blockAccount(acc.accountNumber)
      : this.adminService.unblockAccount(acc.accountNumber);

    apiCall.pipe(finalize(() => this.actionLoading = false))
      .subscribe({
        next: (res) => {
          this.successMessage = `Account ${acc.accountNumber} has been ${isBlocking ? 'BLOCKED' : 'UNBLOCKED'}. Reason: ${reason}`;
          this.loadAdminData();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || `Failed to ${actionText} account.`;
        }
      });
  }
}
