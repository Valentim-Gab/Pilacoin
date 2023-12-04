'use client'

import React, { useState, useRef } from 'react'
import { twMerge } from 'tailwind-merge'
import { Coin } from '@/interfaces/coin'
import './miner.scss'
import { Client } from '@stomp/stompjs'
import { TypeAction, TypeActionWsJson } from '@/interfaces/type-action-ws-json'
import { format } from 'date-fns';

export default function Miner() {
  const [minerPilaMsg, setMinerPilaMsg] = useState('')
  const [validationPilaMsg, setValidationPilaMsg] = useState('')
  const [minerBlockMsg, setMinerBlockMsg] = useState('')
  const [validationBlockMsg, setValidationBlockMsg] = useState('')
  const minerPilaTextareaRef = useRef<HTMLTextAreaElement>(null)
  const validationPilaTextareaRef = useRef<HTMLTextAreaElement>(null)
  const minerBlockTextareaRef = useRef<HTMLTextAreaElement>(null)
  const validationBlockTextareaRef = useRef<HTMLTextAreaElement>(null)

  function scrollToBottom(textareaRef: React.RefObject<HTMLTextAreaElement>) {
    if (textareaRef.current)
      textareaRef.current.scrollTop = textareaRef.current.scrollHeight
  }

  const pilacoinSections = [
    {
      name: 'Mineração Pilacoin',
      icon: 'icon-[solar--chat-round-money-bold]',
      value: minerPilaMsg,
      ref: minerPilaTextareaRef,
    },
    {
      name: 'Validação Pilacoin',
      icon: 'icon-[solar--chat-round-money-bold]',
      value: validationPilaMsg,
      ref: validationPilaTextareaRef,
    },
  ]

  const blockSections = [
    {
      name: 'Mineração Bloco',
      icon: 'icon-[solar--chat-round-money-bold]',
      value: minerBlockMsg,
      ref: minerBlockTextareaRef,
    },
    {
      name: 'Validação Bloco',
      icon: 'icon-[solar--chat-round-money-bold]',
      value: validationBlockMsg,
      ref: validationBlockTextareaRef,
    },
  ]

  const socket = new Client({
    brokerURL: 'ws://localhost:8080/update',
    // debug: function (str) {
    //   console.log(str)
    // },
    reconnectDelay: 5000,
  })

  socket.onConnect = (frame) => {
    let typeActionWsJson: TypeActionWsJson
    socket.subscribe('/topic/pilacoin', (message) => {
      typeActionWsJson = JSON.parse(message.body)
      const formattedTime = format(typeActionWsJson.timestamp ?? new Date(), 'HH:mm:ss');

      function formatMessage(msg: string) {
        msg = (msg) ? msg + '\n' : ''
        msg += `${formattedTime} - ${typeActionWsJson.message}`

        return msg
      }

      switch (typeActionWsJson.type) {
        case TypeAction.MINER_PILACOIN:
          setMinerPilaMsg(formatMessage(minerPilaMsg))
          scrollToBottom(minerPilaTextareaRef)

          break
        case TypeAction.VALIDATION_PILACOIN:
          setValidationPilaMsg(formatMessage(validationPilaMsg))
          scrollToBottom(validationPilaTextareaRef)

          break
        case TypeAction.MINER_BLOCK:
          setMinerBlockMsg(formatMessage(minerBlockMsg))
          scrollToBottom(minerBlockTextareaRef)

          break
        case TypeAction.VALIDATION_BLOCK:
          setValidationBlockMsg(formatMessage(validationBlockMsg))
          scrollToBottom(validationBlockTextareaRef)

          break
        default:
          break
      }
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

  return (
    <div className="flex flex-col w-full">
      <div className="flex flex-col gap-4 w-full border-b py-4 sm:flex-row sm:p-4">
        {pilacoinSections.map((section, index) => (
          <div
            key={index}
            className="flex flex-col justify-center items-center self-stretch px-2 gap-2 w-full"
          >
            <h2>{section.name}</h2>
            <textarea
              className={twMerge(
                'miner-messages flex self-stretch p-2 rounded bg-primary font-light text-xs2',
                'sm:text-xs lg:text-sm lg:py-3'
              )}
              defaultValue={section.value}
              readOnly
              ref={section.ref}
            ></textarea>
          </div>
        ))}
      </div>
      <div className="flex flex-col gap-4 w-full py-4 sm:flex-row sm:p-4">
        {blockSections.map((section, index) => (
          <div
            key={index}
            className="flex flex-col justify-center items-center self-stretch px-2 gap-2 w-full"
          >
            <h2>{section.name}</h2>
            <textarea
              className={twMerge(
                'miner-messages flex self-stretch p-2 rounded bg-primary font-light text-xs2',
                'sm:text-xs lg:text-sm lg:py-3'
              )}
              defaultValue={section.value}
              readOnly
              ref={section.ref}
            ></textarea>
          </div>
        ))}
      </div>
    </div>
  )
}
