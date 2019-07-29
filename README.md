# <table border="0" style="border-collapse:collapse;" cellspacing="0"><tr><td vlign="center"><img src="app/src/main/ic_launcher-web.png" width="50"/></td> <td vlign="center"> Sistema Acadêmico </td> </tr></table> 

Bem-vindo ao projeto Sistema Acadêmico. Esse app tem como objetivo prover acesso ao Q-Acadêmico de instituições federais que utilizam deste sistema.

A aplicação é desenvolvida em 2 módulos principais:
* O aplicativo Sistema Acadêmico em si, desenvolvido em Java utilizando o Android SDK;
* O módulo [QAcadScrapper](https://github.com/MuriloCalegari/QAcadScrapper), desenvolvido na forma de uma biblioteca Java com abstrações que automatizam o processo de extração de dados do Q-Acadêmico.

## Como colaborar

Abra o Android Studio e clique em `File -> New -> Project from Version Control -> Git`, cole a url deste repositório `https://github.com/MuriloCalegari/Ifes-Academico` e então clique em `Clone`. O Android Studio irá fazer o download de todo o código fonte do aplicativo, mas retornará alguns erros durante o _build_ em função de algumas configurações adicionais a serem feitas.

### Configurações de assinatura

Quando você publica um app na Play Store, é necessário que você assine os arquivos que você envia para a loja por uma questão de segurança. Essas configurações de assinatura são definidas no arquivo `app/build.gradle` e, para manter em segredo as minhas chaves de criptografia, os dados são salvos em um arquivo keystore.properties. Para que você possa fazer o build sem as opções de criptografia, é necessário que você delete as linhas que fazem menção à assinatura (signingConfig) do arquivo `app/build.gradle`:

```java
// Load keystore
def keystorePropertiesFile = rootProject.file(".gradle/keystore.properties")
def keystoreProperties = new Properties()
keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

[...]

signingConfigs {
	key0 {
		keyAlias keystoreProperties['keyAlias']
		keyPassword keystoreProperties['keyPassword']
		storeFile file(keystoreProperties['storeFile'])
		storePassword keystoreProperties['storePassword']
	}
}

// A seguir apague somente as linhas *signingConfig signingConfigs.key0*

release {
	minifyEnabled false
	proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	signingConfig signingConfigs.key0
}
debug {
	signingConfig signingConfigs.key0
}
```

### Configurando o Google Services

O aplicativo contém alguns serviços do Google Firebase para coleta de dados de erros. Para que você possa continuar, é necessário que você crie o seu próprio projeto no Google Firebase para fazer o download do arquivo _google-services.json_. Para isso, abra o [console do Google Firebase](https://console.firebase.google.com/u/0/) e clique em `Adicionar projeto`, atribua-o um nome, aceite os termos e clique em `Criar projeto` e então em `Continuar`. Abra o seu novo projeto e clique no ícone do Android para configurar a nova aplicação, em nome do pacote, insira `calegari.murilo.sistema_academico`. Em seguida, faça o download do arquivo _google-services.json_ e coloque-o na raiz do módulo app (_app/_).

O Android Studio agora deve ser capaz de completar o _build_. 
