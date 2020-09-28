import { InjectionToken } from '@angular/core';

export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');

export interface AppConfig {
  apiEndpoint: string;
  minPasswordLength: number;
  maxNameLength: number;
  maxPasswordLength: number;
}
