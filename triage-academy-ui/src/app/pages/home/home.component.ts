import { Component, inject, OnInit } from '@angular/core';
import { NavbarComponent } from '../../components/molecules/navbar/navbar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './home.component.html',
})
/**
 * @description Home page that displays the task list and handles task toggle interactions via the store
 * @export
 * @class HomeComponent
 */
export class HomeComponent {
  
}
