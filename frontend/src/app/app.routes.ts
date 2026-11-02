import { Routes } from '@angular/router';

import { ShellComponent } from './components/shell/shell.component';
import { AuditPageComponent } from './pages/audit/audit-page.component';
import { DashboardPageComponent } from './pages/dashboard/dashboard-page.component';
import { DirectoryPageComponent } from './pages/directory/directory-page.component';
import { EditPageComponent } from './pages/edit/edit-page.component';
import { ProfilePageComponent } from './pages/profile/profile-page.component';
import { RegisterPageComponent } from './pages/register/register-page.component';
import { adminGuard } from './shared/admin.guard';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardPageComponent, data: { title: 'Dashboard' } },
      { path: 'associates', component: DirectoryPageComponent, data: { title: 'Associate directory' } },
      {
        path: 'associates/new',
        component: RegisterPageComponent,
        canActivate: [adminGuard],
        data: { title: 'Register associate' },
      },
      { path: 'associates/:id', component: ProfilePageComponent, data: { title: 'Associate profile' } },
      {
        path: 'associates/:id/edit',
        component: EditPageComponent,
        canActivate: [adminGuard],
        data: { title: 'Edit associate' },
      },
      { path: 'audit', component: AuditPageComponent, data: { title: 'Audit log' } },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
