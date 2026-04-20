import { Component } from '@angular/core';
import * as Notiflix from 'notiflix';

/* Colores del sistema */
const COLOR_PRIMARY = '#512DA8';  // Deep Purple 700
const COLOR_NEUTRAL = '#757575';  // Grey 600
const BORDER_RADIUS = '8px';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Centro de Fisioterapia Kinevid';

  constructor() {
    this.initNotiflix();
  }

  private initNotiflix(): void {

    /* ── Confirm ─────────────────────────────────────────────── */
    Notiflix.Confirm.init({
      borderRadius: BORDER_RADIUS,
      titleColor: COLOR_PRIMARY,
      okButtonBackground: COLOR_PRIMARY,
      cancelButtonBackground: COLOR_NEUTRAL,
    });


    /* ── Loading ─────────────────────────────────────────────── */
    Notiflix.Loading.init({
      svgColor: COLOR_PRIMARY,
    });
  }
}
