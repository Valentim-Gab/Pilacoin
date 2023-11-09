import React from 'react'

interface CryptocurrencyProps {
  name: string
  icon: string
  price: number
  balance: number
}

export default function Cryptocurrency({ name, icon, price, balance }: CryptocurrencyProps) {
  return (
    <div className="crypto-item flex justify-between items-center self-stretch px-2 h-12 border-b">
      <div className="flex justify-center items-center gap-1 self-stretch px-2">
        <span><i className={`${icon} text-2xl`}></i></span>
        <p>{name}</p>
      </div>
      <div className="flex justify-between items-center self-stretch w-40 px-2">
        <p>{price}</p>
        <p>{balance}</p>
      </div>
    </div>
  )
}
