import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { AuthService } from 'src/app/services/auth-service/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    // private toastr: ToastrService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
  }

  onSubmit() {
    this.authService.login({ username: this.loginForm.value.username, password: this.loginForm.value.password }).subscribe(
      result => {
        console.log(result);
        // this.toastr.success('Successful login!');
        localStorage.setItem('token', JSON.stringify(result));
        this.router.navigate(['/homepage']);
      },
      error => {
        // this.toastr.error(error.error);
      }
    );
  }

}
