import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {NotFoundComponent} from './not-found/not-found.component';
import {LoginFormComponent} from './login-form/login-form.component';
import {RegistrationFormComponent} from './registration-form/registration-form.component';
import {MainPageComponent} from './main-page/main-page.component';
import {AuthGuard} from './shared/auth.guard';
import {AdminComponent} from './admin/admin.component';
import {RoleName} from './model/enum/role-name.enum';
import {ExercisePageComponent} from './exercise-page/exercise-page.component';
import {UserWordsComponent} from './user-words/user-words.component';
import {ProfilePageComponent} from './profile-page/profile-page.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginFormComponent
  },
  {
    path: 'registration',
    component: RegistrationFormComponent
  },
  {
    path: 'home',
    component: MainPageComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    component: ProfilePageComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'words',
    component: UserWordsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'exercise',
    component: ExercisePageComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [RoleName.ROLE_ADMIN]
    }
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
    canActivate: [AuthGuard]
  },
  {
    path: '**',
    redirectTo: 'home',
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
