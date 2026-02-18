import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../core/model/auth.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule,CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {



  firstName="";

  lastName="";

  email="";

  password="";

phoneNumber="";

loading=false;

errorMessage="";

  constructor(
    private authService:AuthService,
    private router:Router
  ){

  }

    register() {

    if (!this.firstName || !this.lastName|| !this.email || !this.password) {
      this.errorMessage = 'All fields are required';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: RegisterRequest = {
      firstName: this.firstName,
      lastName: this.lastName,
      email:this.email,
      phoneNumber:this.phoneNumber,
      password: this.password
    };

    this.authService.register(request).subscribe({

      next: () => {
        // token already saved inside AuthService
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration failed';
        this.loading = false;
      }
    });
  }


}
