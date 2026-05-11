import { Component, computed, input } from '@angular/core';

export type BadgeVariant = 'info' | 'success' | 'warning' | 'danger' | 'default';

@Component({
  selector: 'app-badge',
  standalone: true,
  templateUrl: './badge.component.html',
})
export class BadgeComponent {
  text = input.required<string>();
  variant = input<BadgeVariant>('default');

  colorClass = computed(() => {
    const map: Record<BadgeVariant, string> = {
      info:    'bg-blue-100 text-blue-700',
      success: 'bg-green-100 text-green-700',
      warning: 'bg-yellow-100 text-yellow-700',
      danger:  'bg-red-100 text-red-700',
      default: 'bg-gray-100 text-gray-700',
    };
    return map[this.variant()];
  });
}
