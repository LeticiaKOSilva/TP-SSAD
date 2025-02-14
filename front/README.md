# Supermercado Dia

Frontend do Supermercado Dia

## Instalação

1. Clone o repositório:

   ```bash
   git clone https://github.com/LeticiaKOSilva/TP-SSAD.git
   ```

2. Navegue até o diretório "front" do projeto:

   ```bash
   cd TP-SSAD/front
   ```

3. Instale as dependências:

   ```bash
   npm install
   ```

## Uso

Para iniciar o projeto, execute:

```bash
npm run dev
```

Acesse o aplicativo em [http://localhost:5173](http://localhost:5173).

## Aplicação Mobile

O projeto utiliza [Capacitor](https://capacitorjs.com/solution/react) para transformar o aplicativo web em um aplicativo mobile. Para rodar o aplicativo mobile, primeiro faça build do projeto:

```bash
npm run build
```

Depois, rode o comando, de acordo com seu dispositivo, para que o aplicativo seja criado:

```bash
npx cap add android
```

ou

```bash
npx cap add ios
```

Uma pasta "android" ou "ios" será criada e pode ser utilizada, por exemplo, no Android Studio ou no Xcode para rodar o aplicativo.
