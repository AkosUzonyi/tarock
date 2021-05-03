import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ApiService } from '../_services/api.service';
import { CreateGameSessionDialogComponent } from '../create-game-session-dialog/create-game-session-dialog.component';
import { GameSession } from '../_models/dto';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-game-session-list',
  templateUrl: './game-session-list.component.html',
  styleUrls: ['./game-session-list.component.css']
})
export class GameSessionListComponent implements OnInit {
  gameSessions: GameSession[] = [];

  private updateListSubscription: Subscription | null = null;

  constructor(private apiService: ApiService, private dialog: MatDialog) { }

  ngOnInit(): void {
    this.updateList();
    this.updateListSubscription = interval(2000).subscribe(() => this.updateList());
  }

  ngOnDestroy() {
    this.updateListSubscription?.unsubscribe();
  }

  private updateList() {
    this.apiService.getGameSessions()
      .subscribe(g => this.gameSessions = g);
  }

  createGame() {
    const dialogRef = this.dialog.open(CreateGameSessionDialogComponent);
    dialogRef.afterClosed().subscribe(() => this.updateList());
  }

  deleteGameSession(id: number) {
    this.apiService.deleteGameSession(id).subscribe(() => this.updateList());
  }
}