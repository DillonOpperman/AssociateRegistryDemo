import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { RoleService } from '../services/role.service';

export const demoUserInterceptor: HttpInterceptorFn = (request, next) => {
  const role = inject(RoleService);
  return next(
    request.clone({
      setHeaders: {
        'X-Demo-User': role.demoUserLabel(),
      },
    }),
  );
};
