'use client'

import React, { useState } from 'react'
import FormBtn from '@/components/form-btn'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import Image from 'next/image'
import { twMerge } from 'tailwind-merge'
import { Coin } from '@/interfaces/coin'
import './miner.scss'

export default function Miner({ coin }: { coin: Coin }) {
  const realBalance = coin.balance * coin.price
  const mineredCoins = 0
  const mineredCoinsValue = mineredCoins * coin.price
  const [minerMessages, setMinerMessages] = useState('')
  const [selectedOption, setSelectedOption] = useState('')

  function handleOptionChange(event: React.MouseEvent<HTMLButtonElement>) {
    const target = event.target as HTMLInputElement

    setSelectedOption(target.value)
  }

  function miner() {
    setMinerMessages(`${minerMessages}Minerando com ${selectedOption}...\n`)
  }

  return (
    <>
      <div className="flex flex-col items-start self-stretch gap-4 p-4 w-full">
        <div className="flex justify-center items-center gap-1">
          {coin.icon && (
            <i
              className={twMerge(
                `${coin.icon} coin-icon text-2xl sm:text-3xl`,
                coin.name.toLowerCase() == 'pilacoin' ? 'text-gold' : ''
              )}
            ></i>
          )}
          {coin.image && (
            <Image
              src={coin.image}
              width={24}
              height={24}
              alt={coin.name}
              className="sm:w-8 2xl:w-10"
            />
          )}
          <h1 className="font-medium">{coin.name}</h1>
        </div>
        <div className="flex flex-col self-stretch gap-4 sm:flex-row sm:justify-between">
          <div className="flex flex-col gap-1 self-stretch text-xs2 sm:text-sm lg:text-base">
            <p>
              Pilacoins minerados: {mineredCoins.toFixed(2)} (
              {mineredCoinsValue.toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL',
              })}
              )
            </p>
            <p>
              Saldo atual: {coin.balance.toFixed(2)} (
              {realBalance.toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL',
              })}
              )
            </p>
          </div>
          <div className="flex flex-col items-start gap-1 sm:items-end">
            <p className="sm:text-right">Hardware para minerar</p>
            <RadioGroup className="flex gap-4 py-2">
              <div className="flex items-center gap-2">
                <RadioGroupItem
                  value="cpu"
                  id="cpu"
                  className="text-gold hover:bd-gold"
                  onClick={handleOptionChange}
                  checked={selectedOption === 'cpu'}
                />
                <Label htmlFor="cpu" className="cursor-pointer hover:gold">
                  CPU
                </Label>
              </div>
              <div className="flex items-center gap-2">
                <RadioGroupItem
                  value="gpu"
                  id="gpu"
                  className="text-gold hover:bd-gold"
                  onClick={handleOptionChange}
                  checked={selectedOption === 'gpu'}
                />
                <Label htmlFor="gpu" className="cursor-pointer hover:gold">
                  GPU
                </Label>
              </div>
            </RadioGroup>
          </div>
        </div>
        <div className="flex justify-end self-stretch">
          <FormBtn type="button" onClick={miner} disabled={!selectedOption}>
            <i className="icon-[solar--diagram-up-bold]"></i>
            Minerar
          </FormBtn>
        </div>
      </div>
      <div className="flex flex-col justify-center items-center self-stretch px-2 lg:w-7/12 2xl:w-full">
        <textarea
          className="miner-messages flex self-stretch p-2 h-12 rounded bg-primary font-light text-xs2 sm:text-xs lg:text-sm lg:py-3 lg:px-4"
          defaultValue={minerMessages}
          readOnly
        ></textarea>
      </div>
    </>
  )
}
