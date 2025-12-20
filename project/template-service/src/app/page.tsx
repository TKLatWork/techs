'use client';

import Image from "next/image";
import Link from 'next/link'
import { SimpleJsonForms, RawForm } from "./ui/forms/forms";

export default function Home() {
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-8 row-start-2 items-center sm:items-start">
        <div className="flex gap-4 items-center flex-col sm:flex-row">
          <Link href="/api/templateService/templateList" target="_blank" >mockApi/tempateList</Link>
        </div>

        {/* <h3>SimpleJsonForms</h3>
        <SimpleJsonForms /> */}

        <h3>RawForms</h3>
        <RawForm />
      </main>
    </div>
  );
}
