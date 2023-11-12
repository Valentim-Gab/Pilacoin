import { CoinService } from '@/services/coin-service'
import './trade.scss'
import Miner from '@/components/miner'
import { twMerge } from 'tailwind-merge'

export default async function Trade() {
  const coinService = new CoinService()
  const coin = await coinService.getCoin()

  return (
    <main className="trade flex min-h-screen flex-col items-start flex-1 justify-between lg:items-center lg:p-8">
      <section
        className={twMerge(
          'section-trade flex flex-col items-start py-2 border-b w-full',
          'lg:flex-row lg:justify-between lg:border lg:rounded lg:p-4'
        )}
      >
        <Miner coin={coin} />
      </section>
    </main>
  )
}
