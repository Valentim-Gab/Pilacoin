import { Coin } from '@/interfaces/coin'
import { PilacoinService } from './pilacoin-service'

export class CoinService {
  private readonly pilacoinService: PilacoinService

  constructor() {
    this.pilacoinService = new PilacoinService()
  }

  public async getCoin(): Promise<Coin> {
    const pilacoinTot = await this.pilacoinService.findAll()
    let priceTot = (pilacoinTot) ? pilacoinTot?.length * this.pilacoinService.price : 0

    return {
      name: 'PilaCoin',
      icon: 'icon-[solar--chat-round-money-bold]',
      price: priceTot,
      balance: pilacoinTot?.length ?? 0,
    }
  }

  public async getAllCoins(): Promise<Coin[]> {
    return [
      await this.getCoin(),
      {
        name: 'Real Brasileiro',
        image: '/images/icons/brl-icon.png',
        price: 1.0,
        balance: 0.0,
      },
    ]
  }
}
