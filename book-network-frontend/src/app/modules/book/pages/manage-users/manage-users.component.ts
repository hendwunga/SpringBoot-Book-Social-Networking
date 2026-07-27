import {Component, OnInit} from '@angular/core';
import {UserService} from '../../../../services/services/user.service';
import {PageResponseUserResponse} from '../../../../services/models/page-response-user-response';
import {UserResponse} from '../../../../services/models/user-response';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.scss']
})
export class ManageUsersComponent implements OnInit {
  users: PageResponseUserResponse = {};
  page = 0;
  size = 10;
  pages: any = [];
  message = '';
  level: 'success' | 'error' = 'success';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.findAllUsers();
  }

  private findAllUsers() {
    this.userService.findAllUsers({page: this.page, size: this.size}).subscribe({
      next: (resp) => {
        this.users = resp;
        this.pages = Array(this.users.totalPages).fill(0).map((x, i) => i);
      }
    });
  }

  toggleLock(user: UserResponse) {
    this.message = '';
    this.userService.toggleAccountLock(user.id as number).subscribe({
      next: () => {
        user.accountLocked = !user.accountLocked;
        this.level = 'success';
        this.message = `User ${user.firstname} ${user.lastname} ${user.accountLocked ? 'locked' : 'unlocked'}`;
      },
      error: () => {
        this.level = 'error';
        this.message = 'Failed to update user lock status';
      }
    });
  }

  toggleEnabled(user: UserResponse) {
    this.message = '';
    this.userService.toggleAccountEnabled(user.id as number).subscribe({
      next: () => {
        user.enabled = !user.enabled;
        this.level = 'success';
        this.message = `User ${user.firstname} ${user.lastname} ${user.enabled ? 'enabled' : 'disabled'}`;
      },
      error: () => {
        this.level = 'error';
        this.message = 'Failed to update user status';
      }
    });
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllUsers();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllUsers();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllUsers();
  }

  goToLastPage() {
    this.page = this.users.totalPages as number - 1;
    this.findAllUsers();
  }

  goToNextPage() {
    this.page++;
    this.findAllUsers();
  }

  get isLastPage() {
    return this.page === this.users.totalPages as number - 1;
  }
}
