'use client'

import React from 'react'
import Cryptocurrency from '@/components/cryptocurrency'
import './home.scss'
import { Client } from '@stomp/stompjs'
import { PilacoinService } from '@/services/pilacoin-service'

export default function Home() {
  const pilacoinService = new PilacoinService()
  const [totalPilas, setTotalPilas] = React.useState(0)

  const socket = new Client({
    brokerURL: 'ws://localhost:8080/update',
    // debug: function (str) {
    //   console.log(str)
    // },
    reconnectDelay: 5000,
  })

  socket.onConnect = (frame) => {
    //console.log('Conectado ao servidor WebSocket')
    socket.subscribe('/topic/data', (message) => {
      setTotalPilas(Number(message.body))
    })
  }

  socket.onDisconnect = (frame) => {
    if (frame) {
      console.log(
        'Desconectado do servidor WebSocket. Motivo:',
        frame || 'Desconhecido'
      )
    } else {
      console.log('Desconectado do servidor WebSocket')
    }
  }

  socket.onStompError = (frame) => {
    console.error('Erro no WebSocket:', frame.headers.message)
  }

  socket.onWebSocketClose = (event) => {
    console.log('Conexão WebSocket fechada:', event)
  }

  socket.activate()

  const coinsList = [
    {
      name: 'PilaCoin',
      icon: 'icon-[solar--chat-round-money-bold]',
      price: totalPilas * pilacoinService.price,
      balance: totalPilas,
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
          {coinsList &&
            coinsList.map((coin, index) => (
              <li key={index}>
                <Cryptocurrency
                  name={coin.name}
                  icon={coin.icon}
                  image={coin.image}
                  price={coin.price}
                  balance={coin.balance}
                  iconClass={
                    coin.name.toLowerCase() == 'pilacoin'
                      ? 'text-gold'
                      : undefined
                  }
                />
              </li>
            ))}
        </ul>
      </section>
    </main>
  )
}
