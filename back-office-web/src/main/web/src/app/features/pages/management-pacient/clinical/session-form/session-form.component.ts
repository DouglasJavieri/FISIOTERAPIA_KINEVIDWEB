import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'knv-session-form',
  templateUrl: './session-form.component.html',
  styleUrls: ['./session-form.component.scss'],
})
export class SessionFormComponent implements OnInit {
  episodeId!: number;
  sessionId: number | null = null;
  isNew = true;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.episodeId = Number(this.route.snapshot.paramMap.get('episodeId'));
    const sid = this.route.snapshot.paramMap.get('sessionId');
    this.sessionId = sid && sid !== 'new' ? Number(sid) : null;
    this.isNew = !this.sessionId;
  }

  goBack(): void {
    this.router.navigate(['/management-pacient/episodes', this.episodeId, 'sessions']);
  }
}

