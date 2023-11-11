import Image from 'next/image'
import React from 'react'
import './cryptocurrency.scss'

interface CryptocurrencyProps {
  name: string
  icon?: string
  price: number
  balance: number
  image?: string
  iconClass?: string
}

export default function Cryptocurrency({
  name,
  icon,
  price,
  balance,
  image,
  iconClass,
}: CryptocurrencyProps) {
  return (
    <div className="crypto-item flex justify-between items-center self-stretch px-2 h-12 border-b sm:h-16 sm:px-4 2xl:h-20">
      <div className="flex justify-center items-center gap-1 self-stretch px-2 sm:gap-2 2xl:gap-4">
        {icon && (
          <i
            className={`coin-icon ${icon} ${iconClass} text-2xl sm:text-3xl`}
          ></i>
        )}
        {image && (
          <Image
            src={image}
            width={24}
            height={24}
            alt={name}
            className="sm:w-8 2xl:w-10"
          />
        )}
        <h2>{name}</h2>
      </div>
      <div className="flex justify-between items-center self-stretch w-36 px-2 font-medium sm:w-60 2xl:w-80 2xl:text-xl">
        <h3>{price}</h3>
        <h3>{balance}</h3>
      </div>
    </div>
  )
}
