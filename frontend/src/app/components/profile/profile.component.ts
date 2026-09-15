import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { AuthResponse } from '../../models/banking.models';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: false
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  passwordModalForm: FormGroup;

  user: AuthResponse | null = null;
  showPasswordModal: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';
  modalErrorMessage: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.pattern('^\\+?[0-9]{10,15}$')]],
      newPassword: [''],
      confirmPassword: ['']
    });

    this.passwordModalForm = this.fb.group({
      currentPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(currUser => {
      if (currUser) {
        this.user = currUser;
        this.profileForm.patchValue({
          firstName: currUser.firstName,
          lastName: currUser.lastName,
          email: currUser.email,
          phoneNumber: '+1999888777'
        });
      }
    });
  }

  get initials(): string {
    if (!this.user) return 'U';
    return (this.user.firstName.charAt(0) + this.user.lastName.charAt(0)).toUpperCase();
  }

  openPasswordModal(): void {
    if (this.profileForm.invalid) return;

    const newPass = this.profileForm.value.newPassword;
    const confirmPass = this.profileForm.value.confirmPassword;

    if (newPass && newPass !== confirmPass) {
      this.errorMessage = 'New Password and Confirm Password do not match.';
      return;
    }

    this.errorMessage = '';
    this.modalErrorMessage = '';
    this.passwordModalForm.reset();
    this.showPasswordModal = true;
  }

  closeModal(): void {
    this.showPasswordModal = false;
  }

  confirmProfileUpdate(): void {
    if (this.passwordModalForm.invalid) return;

    this.loading = true;
    this.modalErrorMessage = '';
    this.successMessage = '';

    const payload = {
      firstName: this.profileForm.value.firstName,
      lastName: this.profileForm.value.lastName,
      email: this.profileForm.value.email,
      phoneNumber: this.profileForm.value.phoneNumber,
      currentPassword: this.passwordModalForm.value.currentPassword,
      newPassword: this.profileForm.value.newPassword || undefined
    };

    this.authService.updateProfile(payload)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (res) => {
          this.showPasswordModal = false;
          this.successMessage = 'Profile & security credentials updated successfully in database!';
          this.profileForm.patchValue({ newPassword: '', confirmPassword: '' });
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.modalErrorMessage = err.error?.message || 'Current password verification failed. Please try again.';
          this.cdr.detectChanges();
        }
      });
  }
}
