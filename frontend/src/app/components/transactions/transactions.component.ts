import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { BankAccount, PagedResponse, Transaction } from '../../models/banking.models';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css'],
  standalone: false
})
export class TransactionsComponent implements OnInit {
  myAccounts: BankAccount[] = [];
  selectedAccount: string = '';
  filterType: string = '';
  startDate: string = '';
  endDate: string = '';

  pagedTransactions?: PagedResponse<Transaction>;
  loading: boolean = false;
  currentPage: number = 0;
  pageSize: number = 10;

  constructor(
    private accountService: AccountService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.accountService.getMyAccounts().subscribe(res => {
      this.myAccounts = res.data || [];
      if (this.myAccounts.length > 0) {
        this.selectedAccount = this.myAccounts[0].accountNumber;
        this.loadTransactions();
      }
    });
  }

  onAccountChange(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  loadTransactions(): void {
    if (!this.selectedAccount) return;

    this.loading = true;
    this.transactionService.getStatement(
      this.selectedAccount,
      this.filterType ? this.filterType : undefined,
      this.startDate ? this.startDate + 'T00:00:00' : undefined,
      this.endDate ? this.endDate + 'T23:59:59' : undefined,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (res) => {
        this.pagedTransactions = res.data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onFilterSubmit(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  downloadPDFStatement(): void {
    if (!this.selectedAccount || !this.pagedTransactions) return;

    const account = this.myAccounts.find(a => a.accountNumber === this.selectedAccount);
    const printWindow = window.open('', '_blank');
    if (!printWindow) return;

    const rowsHtml = this.pagedTransactions.content.map(t => `
      <tr>
        <td style="padding: 10px; border-bottom: 1px solid #ddd; font-family: monospace;">${t.transactionReference}</td>
        <td style="padding: 10px; border-bottom: 1px solid #ddd; font-weight: bold;">${t.type}</td>
        <td style="padding: 10px; border-bottom: 1px solid #ddd;">${t.sourceAccountNumber} ${t.targetAccountNumber ? '➔ ' + t.targetAccountNumber : ''}</td>
        <td style="padding: 10px; border-bottom: 1px solid #ddd; font-weight: bold; color: ${t.type === 'DEPOSIT' || t.targetAccountNumber === this.selectedAccount ? '#10b981' : '#ef4444'};">
          ${t.type === 'DEPOSIT' || t.targetAccountNumber === this.selectedAccount ? '+' : '-'}₹${t.amount.toFixed(2)}
        </td>
        <td style="padding: 10px; border-bottom: 1px solid #ddd;">${t.description}</td>
        <td style="padding: 10px; border-bottom: 1px solid #ddd; font-size: 12px;">${new Date(t.createdAt).toLocaleString()}</td>
      </tr>
    `).join('');

    const htmlContent = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>AetherBank Account Statement - ${this.selectedAccount}</title>
        <style>
          body { font-family: sans-serif; padding: 40px; color: #0f172a; }
          .header { display: flex; justify-content: space-between; border-bottom: 2px solid #4f46e5; padding-bottom: 20px; margin-bottom: 30px; }
          .title { font-size: 24px; font-weight: bold; color: #4f46e5; }
          table { width: 100%; border-collapse: collapse; margin-top: 20px; }
          th { background: #f1f5f9; padding: 12px; text-align: left; font-size: 12px; text-transform: uppercase; border-bottom: 2px solid #cbd5e1; }
        </style>
      </head>
      <body>
        <div class="header">
          <div>
            <div class="title">🏛️ AetherBank Statement</div>
            <div>Account Number: <strong>${this.selectedAccount}</strong></div>
            <div>Account Type: ${account ? account.accountType : 'SAVINGS'}</div>
          </div>
          <div style="text-align: right;">
            <div>Generated: ${new Date().toLocaleDateString()}</div>
            <div>Current Balance: <strong>₹${account ? account.balance.toFixed(2) : '0.00'}</strong></div>
          </div>
        </div>

        <h3>Official Transaction Audit Ledger</h3>
        <table>
          <thead>
            <tr>
              <th>Reference</th>
              <th>Type</th>
              <th>Counterparty Details</th>
              <th>Amount</th>
              <th>Note</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            ${rowsHtml}
          </tbody>
        </table>

        <script>
          window.onload = function() { window.print(); }
        </script>
      </body>
      </html>
    `;

    printWindow.document.write(htmlContent);
    printWindow.document.close();
  }

  nextPage(): void {
    if (this.pagedTransactions && !this.pagedTransactions.last) {
      this.currentPage++;
      this.loadTransactions();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTransactions();
    }
  }
}