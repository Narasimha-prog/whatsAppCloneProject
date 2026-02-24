import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule,FormsModule,RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {


  email = '';
  password = '';
  errorMessage = '';
  loading=false;


  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.loading = true;
    this.errorMessage = '';
  this.authService.login({
    body: { email:this.email,password: this.password}
  }).subscribe({
    next: () => {
      this.loading = false;
      this.router.navigate(['/']);
    },
    error: (err) => {
      this.loading = false;
      this.errorMessage = err.error?.message || 'Login failed';
    }
  });

}
}