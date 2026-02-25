import { Routes } from '@angular/router';
import { Main } from './pages/main/main';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';

export const routes: Routes = [
    {
        path: '',
        component: Main
    }
    ,{
        path:'login',
        component: Login

    },
    {
        path: 'register',
        component: Register
    },
    // {
    //     // path:'profile',
    //     // component:
    // }
];
