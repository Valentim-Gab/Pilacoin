import { CoinService } from '@/services/coin-service'
import './trade.scss'
import Miner from '@/components/miner'
import { twMerge } from 'tailwind-merge'
import { PilacoinService } from '@/services/pilacoin-service'
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import FormBtn from '@/components/form-btn'
import Link from 'next/link'

export default async function Trade() {
  const coinService = new CoinService()
  const pilacoinService = new PilacoinService()
  const coin = await coinService.getCoin()

  const pilacoinList = await pilacoinService.findAll()

  function formatDate(oldDate: Date) {
    const date = new Date(oldDate)
    const dateFormat = new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    })

    return dateFormat.format(date)
  }

  return (
    <main className="trade flex min-h-screen flex-col items-start flex-1 lg:items-center lg:p-8 gap-4">
      <section
        className={twMerge(
          'section-trade flex flex-col items-start py-2 border-b w-full',
          'lg:flex-row lg:justify-between lg:border lg:rounded lg:p-4'
        )}
      >
        <Miner coin={coin} />
      </section>

      <section
        className={twMerge(
          'section-trade flex flex-col items-start py-2 border-b w-full',
          'lg:border lg:rounded'
        )}
      >
        <h1 className="p-2 sm:p-4">Transferir Pilacoin</h1>
        <Table>
          <TableCaption className="pb-4">
            Pilacoins listados separadamente
          </TableCaption>
          <TableHeader>
            <TableRow className="lg:text-xl">
              <TableHead className="text-right">ID</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Criação</TableHead>
              <TableHead>Nonce</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody className="py-4">
            {pilacoinList &&
              pilacoinList.length > 0 &&
              pilacoinList.map((pilacoin) => (
                <TableRow className="text-xs sm:text-sm lg:text-base" key={pilacoin.id}>
                  <TableCell className="text-right">{pilacoin.id}</TableCell>
                  <TableCell>{pilacoin.status}</TableCell>
                  <TableCell>
                    {pilacoin.dataCriacao
                      ? formatDate(pilacoin.dataCriacao)
                      : 'Indefinido'}
                  </TableCell>
                  <TableCell className="nonce">{pilacoin.nonce}</TableCell>
                  <TableCell>
                    <Link
                      href={`/pilacoin/${pilacoin.nonce}`}
                      className="flex gap-1 items-center justify-center"
                    >
                      <i className="icon-[solar--card-transfer-bold]"></i>
                      Transferir
                    </Link>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </section>
    </main>
  )
}
