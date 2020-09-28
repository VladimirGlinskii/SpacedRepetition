import {Component, OnInit} from '@angular/core';
import {RoleName} from '../model/enum/role-name.enum';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ChangePasswordModalComponent} from './change-password-modal/change-password-modal.component';
import {ApiUserService} from '../service/api-user.service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {

  constructor(private modalService: NgbModal,
              public userService: ApiUserService) {
  }

  ngOnInit(): void {
  }

  getUsername(): string {
    return localStorage.getItem('username');
  }

  getEmail(): string {
    return localStorage.getItem('email');
  }

  getRoles(): string {
    let rolesString = '';
    (JSON.parse(localStorage.getItem('roles')) as RoleName[]).forEach((roleName, index, array) => {
      rolesString += RoleName[roleName].substring(5);
      if (index < array.length - 1) {
        rolesString += ', ';
      }
    });
    return rolesString;
  }

  changePassword() {
    const modalRef = this.modalService.open(ChangePasswordModalComponent);
    modalRef.componentInstance.model.id = localStorage.getItem('id');
  }

}
