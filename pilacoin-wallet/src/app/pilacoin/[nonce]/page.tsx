import { PilacoinService } from '@/services/pilacoin-service'
import { UserService } from '@/services/user-service'
import React from 'react'
import './pilacoin.scss'
import Transfer from './transfer'
import { User } from '@/interfaces/user'

export default async function PilacoinById({
  params,
}: {
  params: { nonce: string }
}) {
  const pilacoinService = new PilacoinService()
  const userService = new UserService()
  const userList = await userService.findAll()
  const pilacoin = await pilacoinService.findOneByNonce(params.nonce)
  const user: User = await userService.getLoggedUser()

  return (
    <main className="pilacoin flex min-h-screen flex-col flex-1 items-center lg:p-8 gap-4">
      <h1 className="mt-4">Transferir Pilacoin</h1>
      <section className="section-pilacoin flex flex-col items-start p-2 border-b w-full lg:border lg:rounded lg:p-4">
        <p>{`Id: ${pilacoin?.id ?? ''}`}</p>
        <p>{`Status: ${pilacoin?.status ?? ''}`}</p>
        <p>{`Criador: ${pilacoin?.nomeCriador ?? ''}`}</p>
        <p>{`Chave: ${pilacoin?.chaveCriador ?? ''}`}</p>
        <p>{`Nonce: ${pilacoin?.nonce ?? ''}`}</p>
      </section>

      <section className="section-user flex flex-col items-start gap-4 p-2 border-b w-full lg:border lg:rounded lg:p-4">
        <h2>Selecione um usuário para transferir</h2>
        {userList && userList.length > 0 && pilacoin && (
          <Transfer userList={userList} pilacoin={pilacoin} userLogged={user} />
        )}
      </section>
    </main>
  )
}
