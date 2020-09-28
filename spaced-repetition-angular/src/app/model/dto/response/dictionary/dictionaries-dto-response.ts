import {Dictionary} from '../../../dictionary';

export interface DictionariesDtoResponse {
  dictionaries: Dictionary[];
  totalPages: number;
}
