import { Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';

import { DemoRole, RoleService } from '../../services/role.service';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatFormField, MatLabel, MatSelect, MatOption],
  templateUrl: './shell.html',
})
export class ShellComponent {
  readonly role = inject(RoleService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly title = inject(Title);

  readonly pageTitle = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(() => this.currentTitle()),
    ),
    { initialValue: 'Dashboard' },
  );

  constructor() {
    effect(() => {
      this.title.setTitle(`${this.pageTitle()} · Associate Registry`);
    });
  }

  onRole(value: unknown): void {
    if (value !== 'ADMINISTRATOR' && value !== 'READ_ONLY') {
      return;
    }
    const role = value as DemoRole;
    this.role.setRole(role);
    const url = this.router.url;
    if (role === 'READ_ONLY' && (url.includes('/edit') || url.includes('/associates/new'))) {
      void this.router.navigate(['/associates']);
    }
  }

  private currentTitle(): string {
    let current: ActivatedRoute | null = this.activatedRoute;
    while (current?.firstChild) {
      current = current.firstChild;
    }
    const title = current?.snapshot?.data?.['title'];
    return typeof title === 'string' ? title : 'Associate Registry';
  }
}
