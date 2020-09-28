import { TestBed } from '@angular/core/testing';

import { ApiExerciseService } from './api-exercise.service';

describe('ApiExerciseService', () => {
  let service: ApiExerciseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiExerciseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
