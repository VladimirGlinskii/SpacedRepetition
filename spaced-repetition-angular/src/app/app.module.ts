import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NotFoundComponent} from './not-found/not-found.component';
import {NavigationComponent} from './navigation/navigation.component';
import {LoginFormComponent} from './login-form/login-form.component';
import {RegistrationFormComponent} from './registration-form/registration-form.component';
import {FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {APP_CONFIG} from './app-config';
import {MainPageComponent} from './main-page/main-page.component';
import {JwtInterceptor} from './shared/jwt.interceptor';
import { DictionaryWordsComponent } from './admin/admin-panel-dictionaries/dictionary-words/dictionary-words.component';
import {AdminComponent} from './admin/admin.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {WordsComponent} from './user-words/words/words.component';
import {ExercisePageComponent} from './exercise-page/exercise-page.component';
import {AddWordFormComponent} from './admin/add-word-form/add-word-form.component';
import {AddDictionaryFormComponent} from './admin/add-dictionary-form/add-dictionary-form.component';
import {AutofocusFixModule} from 'ngx-autofocus-fix';
import {HttpErrorInterceptor} from './shared/http-error.interceptor';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import { UsersListComponent } from './admin/users-list/users-list.component';

import {MDBBootstrapModule} from 'angular-bootstrap-md';
import { AdminPanelDictionariesComponent } from './admin/admin-panel-dictionaries/admin-panel-dictionaries.component';
import { UserWordsComponent } from './user-words/user-words.component';
import { EditWordComponent } from './admin/edit-word/edit-word.component';
import { AdminPanelWordsComponent } from './admin/admin-panel-words/admin-panel-words.component';
import { DeleteWordModalComponent } from './admin/delete-word-modal/delete-word-modal.component';
import { DeleteUserModalComponent } from './admin/delete-user-modal/delete-user-modal.component';
import { DeleteDictionaryModalComponent } from './admin/delete-dictionary-modal/delete-dictionary-modal.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { ChangePasswordModalComponent } from './profile-page/change-password-modal/change-password-modal.component';
import { RenameDictionaryModalComponent } from './admin/rename-dictionary-modal/rename-dictionary-modal.component';
import { SelectDictionaryModalComponent } from './user-words/select-dictionary-modal/select-dictionary-modal.component';
import { UnselectDictionaryModalComponent } from './user-words/unselect-dictionary-modal/unselect-dictionary-modal.component';

@NgModule({
  declarations: [
    AppComponent,
    NotFoundComponent,
    NavigationComponent,
    LoginFormComponent,
    RegistrationFormComponent,
    MainPageComponent,
    AdminComponent,
    WordsComponent,
    ExercisePageComponent,
    AddWordFormComponent,
    AddDictionaryFormComponent,
    DictionaryWordsComponent,
    UsersListComponent,
    AdminPanelDictionariesComponent,
    UserWordsComponent,
    EditWordComponent,
    AdminPanelWordsComponent,
    DeleteWordModalComponent,
    DeleteUserModalComponent,
    DeleteDictionaryModalComponent,
    ProfilePageComponent,
    ChangePasswordModalComponent,
    RenameDictionaryModalComponent,
    SelectDictionaryModalComponent,
    UnselectDictionaryModalComponent
  ],
  imports: [
    NgbModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule,
    AutofocusFixModule.forRoot(),
    MatSnackBarModule,
    MatButtonModule,
    MDBBootstrapModule.forRoot()
  ],
  providers: [
    {
      provide: APP_CONFIG,
      useValue: {
        /*apiEndpoint: 'http://10.69.9.192:8888/api/',*/
        apiEndpoint: 'http://localhost:8765/api/',
        minPasswordLength: 5,
        maxNameLength: 20,
        maxPasswordLength: 25
      }
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
