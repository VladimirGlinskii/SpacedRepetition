import {Component, OnInit} from '@angular/core';
import {ApiAdminService} from '../../service/api-admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ErrorCode} from '../../exception/error-code.enum';
import {User} from '../../model/user';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteUserModalComponent} from '../delete-user-modal/delete-user-modal.component';
import {RoleName} from '../../model/enum/role-name.enum';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css']
})
export class UsersListComponent implements OnInit {

  users: User[];
  page: number;
  pageSize: number;
  totalPages: number;
  searchTerm: string;

  constructor(private adminService: ApiAdminService,
              private snackBar: MatSnackBar,
              private modalService: NgbModal) {
    this.pageSize = 10;
    this.searchTerm = '';
  }

  ngOnInit(): void {
  }

  handleError(code: ErrorCode): void {
    if (code !== ErrorCode.CLIENT_SIDE_ERROR) {
      this.snackBar.open(ErrorCode[code], null, {
        duration: 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar']
      });
    }
  }

  getUsers(page: number): void {
    this.adminService.getUsers(page, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.users = res.users;
        this.page = 0;
        this.totalPages = res.totalPages;
      },
      error => this.handleError(error)
    );
  }

  filter(): void {
    const tmpSearchTerm = this.searchTerm;
    setTimeout(() => {
      if (tmpSearchTerm === this.searchTerm) {
        this.getUsers(0);
      }
    }, 1000);
  }

  resetPage(): void {
    this.page = null;
    this.totalPages = null;
    this.users = null;
  }

  toBegin(): void {
    this.adminService.getUsers(0, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = 0;
        this.totalPages = res.totalPages;
        this.users = res.users;
      },
      error => this.handleError(error)
    );
  }

  toEnd(): void {
    this.adminService.getUsers(this.totalPages - 1, this.pageSize, this.searchTerm).subscribe(
      res => {
        this.page = res.totalPages - 1;
        this.totalPages = res.totalPages;
        this.users = res.users;
      },
      error => this.handleError(error)
    );
  }

  next(): void {
    if (this.page < this.totalPages - 1) {
      this.adminService.getUsers(this.page + 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page++;
          this.totalPages = res.totalPages;
          this.users = res.users;
        },
        error => this.handleError(error)
      );
    }
  }

  previous(): void {
    if (this.page > 0) {
      this.adminService.getUsers(this.page - 1, this.pageSize, this.searchTerm).subscribe(
        res => {
          this.page--;
          this.totalPages = res.totalPages;
          this.users = res.users;
        },
        error => this.handleError(error)
      );
    }
  }

  remove(user: User) {
    const modalRef = this.modalService.open(DeleteUserModalComponent);
    modalRef.componentInstance.userModel = user;
    modalRef.result.then(result => {
      if (result === 'Deleted') {
        this.getUsers(this.page);
      }
    });
  }

  isNotAdmin(user: User): boolean {
    return user.roles.every(role => role !== RoleName.ROLE_ADMIN);
  }
}
