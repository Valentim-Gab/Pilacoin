import FormUser from "@/components/form-user";

export default function Account() {
  return (
    <main className="flex min-h-screen flex-col items-start flex-1 self-stretch">
      <section className="flex flex-col items-center self-stretch p-2 gap-2">
        <FormUser />
      </section>
    </main>
  )
}
