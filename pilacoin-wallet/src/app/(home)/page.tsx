import Cryptocurrency from "@/components/cryptocurrency";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-start flex-1 self-stretch">
      <Cryptocurrency name="PilaCoin" icon='icon-[solar--chat-round-money-bold] gold' price={144.20} balance={23.00} />
    </main>
  )
}
