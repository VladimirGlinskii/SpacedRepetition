import {Component, OnInit, ViewChild} from '@angular/core';
import {UsersListComponent} from './users-list/users-list.component';
import {AdminPanelDictionariesComponent} from './admin-panel-dictionaries/admin-panel-dictionaries.component';
import {AdminPanelWordsComponent} from './admin-panel-words/admin-panel-words.component';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  @ViewChild(UsersListComponent) usersList: UsersListComponent;
  @ViewChild(AdminPanelDictionariesComponent) dictionariesComponent: AdminPanelDictionariesComponent;
  @ViewChild(AdminPanelWordsComponent) wordsComponent: AdminPanelWordsComponent;

  constructor() {
  }

  ngOnInit(): void {
  }

  resetPage(): void {
    this.usersList.resetPage();
    this.dictionariesComponent.resetPage();
    this.wordsComponent.resetPage();
  }

  getUsers(): void {
    this.resetPage();
    this.usersList.getUsers(0);
  }

  getDictionaries(): void {
    this.resetPage();
    this.dictionariesComponent.getDictionaries(0);
  }

  getWords(): void {
    this.resetPage();
    this.wordsComponent.getWords(0);
  }

}
