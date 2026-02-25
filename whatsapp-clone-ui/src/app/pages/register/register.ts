import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth';
import { Router, RouterLink } from '@angular/router';
import { RegisterUserRequest } from '../../api/models';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule,CommonModule,RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {



  firstName="";

  lastName="";

  email="";

  password="";

  phoneNumber="";

  loading:boolean=false;

  errorMessage="";

  showPassword: boolean = false;

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

    const request: RegisterUserRequest = {
      firstName: this.firstName,
      lastName: this.lastName,
      email:this.email,
      phoneNumber:this.phoneNumber,
      password: this.password
    };

    this.authService.register({
      body:request
    }).subscribe({
      next: (value) =>{
            this.loading = false;
             this.router.navigate(['/']);
      },
      error: (err ) =>{
        this.loading=false;
        this.errorMessage=err.error?.message;
      }
    })
  }
togglePassword() {
  this.showPassword = !this.showPassword;
}

}
