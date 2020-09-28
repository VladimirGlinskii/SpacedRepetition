import {Component, OnInit} from '@angular/core';
import {ApiUserService} from '../service/api-user.service';
import {ErrorsResponse} from '../model/dto/response/errors-response';
import {ErrorResponse} from '../model/dto/response/error-response';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NavigationEnd, Router} from '@angular/router';
import {WeeklyStatisticsResponse} from '../model/dto/response/statistics/weekly-statistics-response';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {


  public chartType = 'bar';

  public chartDatasets: Array<any> = [
    {data: [0, 0, 0, 0, 0, 0, 0, 0], label: '% of correct answers for last 7 days'}
  ];

  public chartLabels: Array<any> = ['', '', '', '', '', '', ''];

  public chartColors: Array<any> = [
    {
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(54, 162, 235, 0.2)'
      ],
      borderColor: [
        'rgba(255,99,132,1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)',
        'rgba(54, 162, 235, 1)'
      ],
      borderWidth: 2,
    }
  ];

  public chartOptions: any = {
    responsive: true
  };

  constructor(private userService: ApiUserService,
              private snackBar: MatSnackBar,
              private router: Router) {
    router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (event.url === '/home') {
          this.getStatistics();
        } else {
        }
      }
    });
  }

  ngOnInit(): void {
  }

  public chartClicked(e: any): void {
  }

  public chartHovered(e: any): void {
  }

  handleError(errorsResponse: ErrorsResponse): void {
    if (errorsResponse.errors != null) {
      const errors: ErrorResponse[] = errorsResponse.errors;
      for (let i = 0; i < errorsResponse.errors.length; i++) {
        this.snackBar.open(errors[i].message, null, {
          duration: (i + 1) * 3000, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['standard-snackbar']
        });
      }
    }
  }

  updateOnlyDatasets(response: WeeklyStatisticsResponse) {
    const chartData = response.weeklyStatistics.map(s => {
      let percent = s.correctAnswers + s.wrongAnswers;
      if (percent > 0) {
        percent = Math.round((s.correctAnswers / percent) * 100);
      }
      return percent;
    });
    chartData.push(100);
    this.chartDatasets = [
      {data: chartData, label: '% of correct answers for last 7 days'},
    ];
  }

  updateChartMonthsLabels(response: WeeklyStatisticsResponse) {
    this.chartLabels = response.weeklyStatistics.map(s => s.date.toString());
  }

  getStatistics(): void {
    this.userService.getWeeklyStatistics().subscribe(
      res => {
        this.updateOnlyDatasets(res);
        this.updateChartMonthsLabels(res);
      },
      error => {
        this.handleError(error);
        throw error;
      }
    );
  }

}
