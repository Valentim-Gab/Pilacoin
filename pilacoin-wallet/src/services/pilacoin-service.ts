import { Pilacoin } from "@/interfaces/pilacoin"
import { Transaction } from '@/interfaces/transaction'

export class PilacoinService {
  private readonly apiUrl = process.env.API_URL ?? 'http://localhost:8080'
  private readonly endpoint = `${this.apiUrl}/pilacoin`
  public readonly price = 144.2

  public async findAll(): Promise<Pilacoin[] | null> {
    try {
      const response = await fetch(this.endpoint, { cache: 'no-store' })

      if (response.ok && response.status == 200) {  
        const data = await response.json()

        return data
      }
    } catch (err) {
      console.error(err)
    }

    return null
  }

  public async findOneByNonce(nonce: string): Promise<Pilacoin | null> {
    try {
      const response = await fetch(`${this.endpoint}/${nonce}`)

      if (response.ok && response.status == 200) {  
        const data = await response.json()

        return data
      }
    } catch (err) {
      console.error(err)
    }

    return null
  }

  public async transferOne(transaction: Transaction): Promise<Transaction | Boolean> {
    try {
      const response = await fetch(`${this.endpoint}/transfer`, {
        method: 'POST',
        body: JSON.stringify(transaction),
        headers: {
          'Content-Type': 'application/json'
        }
      })

      if (response.ok && response.status == 200) {  
        const transaction = await response.json()

        return transaction
      }

      const isError = await response.json()

      return isError
    } catch (err) {
      console.error(err)
    }

    return false
  }
}