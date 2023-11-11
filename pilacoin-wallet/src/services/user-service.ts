import { User } from '@/interfaces/user'

export class UserService {
  public async getUser(): Promise<User> {
    return {
      name: 'Gabriel Valentim',
      username: 'Valentim-Gab',
      email: 'gabriel@gmail.com',
    }
  }
}
