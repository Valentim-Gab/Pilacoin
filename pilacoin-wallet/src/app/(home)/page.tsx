import React from 'react'
import Cryptocurrency from '@/components/cryptocurrency'
import './home.scss'

export default function Home() {
  const coins = [
    {
      name: 'PilaCoin',
      icon: 'icon-[solar--chat-round-money-bold] gold',
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

  return (
    <main className="home flex min-h-screen flex-col items-center flex-1 self-stretch lg:py-8">
      <section className="section-coins w-full flex flex-col flex-start lg:border lg:rounded">
        <div className="flex justify-between items-center self-stretch p-2 border-b text-xs2 sm:px-4 sm:text-sm lg:text-base">
          <div className="flex justify-between items-center self-stretch gap-1 px-2">
            <p>Moeda</p>
          </div>
          <div className="flex w-36 py-1 px-2 justify-between items-center sm:w-60 2xl:w-80">
            <p>Preço (R$)</p>
            <p>Saldo</p>
          </div>
        </div>
        <ul className="w-full">
          {coins &&
            coins.map((coin, index) => (
              <li key={index}>
                <Cryptocurrency
                  name={coin.name}
                  icon={coin.icon}
                  image={coin.image}
                  price={coin.price}
                  balance={coin.balance}
                />
              </li>
            ))}
        </ul>
      </section>
    </main>
  )
}
