export class RegisterDtoRequest {
  username: string;
  email: string;
  password: string;

  constructor(username: string, email: string, password: string) {
    this.password = password;
    this.email = email;
    this.username = username;
  }
}
