import { User } from '@/interfaces/user'

export class UserService {
  private readonly apiUrl = process.env.API_URL
  private readonly endpoint = `${this.apiUrl}/user`

  public async findAll(): Promise<User[] | null> {
    try {
      const response = await fetch(this.endpoint, {
        cache: 'no-cache',
      })

      if (response.ok && response.status == 200) {  
        const data = await response.json()

        return data
      }
    } catch (err) {
      console.error(err)
    }

    return null
  }

  public async getLoggedUser(): Promise<User> {
    return {
      nome: 'Gabriel_Valentim',
      username: 'Valentim-Gab',
      email: 'gabriel@gmail.com',
    }
  }
}
