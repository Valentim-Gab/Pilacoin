import { Coin } from '@/interfaces/coin'

export class CoinService {
  public async getCoin(): Promise<Coin> {
    return {
      name: 'PilaCoin',
      icon: 'icon-[solar--chat-round-money-bold]',
      price: 144.2,
      balance: 23.0,
    }
  }

  public async getAllCoins(): Promise<Coin[]> {
    return [
      {
        name: 'PilaCoin',
        icon: 'icon-[solar--chat-round-money-bold]',
        price: 144.2,
        balance: 23.0,
      },
      {
        name: 'Real Brasileiro',
        image: '/images/icons/brl-icon.png',
        price: 1.0,
        balance: 0.0,
      },
    ]
  }
}
