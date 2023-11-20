import { PilacoinService } from '@/services/pilacoin-service'
import { UserService } from '@/services/user-service'
import React from 'react'
import { twMerge } from 'tailwind-merge'
import './pilacoin.scss'

export default async function PilacoinById({
  params,
}: {
  params: { nonce: string }
}) {
  const pilacoinService = new PilacoinService()
  const userService = new UserService()
  const userList = await userService.findAll()
  const pilacoin = await pilacoinService.findOneByNonce(params.nonce)

  return (
    <main className="pilacoin flex min-h-screen flex-col flex-1 items-center lg:p-8 gap-4">
      <h1 className="mt-4">Transferir Pilacoin</h1>
      <section
        className={twMerge(
          'section-pilacoin flex flex-col items-start p-2 border-b w-full',
          'lg:flex-row lg:justify-between lg:border lg:rounded lg:p-4'
        )}
      >
        <p>{`Id: ${pilacoin?.id ?? ''}`}</p>
        <p>{`Status: ${pilacoin?.status ?? ''}`}</p>
        <p>{`Criador: ${pilacoin?.nomeCriador ?? ''}`}</p>
        <p>{`Chave: ${pilacoin?.chaveCriador ?? ''}`}</p>
        <p>{`Nonce: ${pilacoin?.nonce ?? ''}`}</p>
      </section>

      <section
        className={twMerge(
          'section-user flex flex-col items-start p-2 border-b w-full',
          'lg:flex-row lg:justify-between lg:border lg:rounded lg:p-4'
        )}
      >
        <h2>Usuários para transferir</h2>
        <ul>
          {userList &&
            userList.length > 0 &&
            userList.map((user) => (
              <li className="p-2 border" key={user.nome}>
                {user.nome}
              </li>
            ))}
        </ul>
      </section>
    </main>
  )
}
