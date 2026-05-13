# DoeFlow

## 1. Objetivo de Resolução de Problema

Muitas pessoas querem doar para causas importantes, mas não confiam totalmente para onde o dinheiro vai.

O **DoeFlow** resolve isso criando campanhas transparentes, com meta, progresso público e controle administrativo.

---

## 2. Público-Alvo / Quem Usa

### Visitante
Entra no site, vê campanhas e pode doar.

### Doador
Faz login, doa e acompanha suas doações.

### Criador de Campanha
Cria campanhas, edita informações e acompanha a arrecadação.

### Administrador
Aprova campanhas, gerencia usuários e verifica doações.

---

## 3. Funcionalidades Principais

### Campanhas

- Criar campanha
- Editar campanha
- Listar campanhas
- Ver detalhes da campanha
- Exibir meta e valor arrecadado
- Status da campanha:
  - Pendente
  - Aprovada
  - Recusada
  - Finalizada

### Usuários

- Cadastro
- Login
- Perfil
- Tipo de usuário:
  - Doador
  - Criador
  - Admin

### Doações

- Registrar doação
- Informar valor
- Vincular doação a uma campanha
- Atualizar progresso da campanha

### Admin

- Aprovar ou recusar campanhas
- Ver todas as campanhas
- Ver usuários
- Ver doações

---

## 4. Telas do Sistema

- Página inicial
- Página de campanhas
- Página de detalhes da campanha
- Página de login
- Página de cadastro
- Dashboard do criador
- Dashboard do admin
- Página de minhas doações
- Página de criar campanha
- Página de editar campanha

---

## 5. Banco de Dados

### Entidades Principais

- User
- Campaign
- Donation
- Category
- CampaignImage

---

### User

| Campo | Descrição |
|---|---|
| id | Identificador do usuário |
| name | Nome do usuário |
| email | E-mail do usuário |
| password | Senha do usuário |
| role | Tipo de usuário |

---

### Campaign

| Campo | Descrição |
|---|---|
| id | Identificador da campanha |
| title | Título da campanha |
| description | Descrição da campanha |
| goalAmount | Valor da meta |
| currentAmount | Valor arrecadado atualmente |
| status | Status da campanha |
| createdBy | Usuário que criou a campanha |
| category | Categoria da campanha |
| imageUrl | Imagem principal da campanha |

---

### Donation

| Campo | Descrição |
|---|---|
| id | Identificador da doação |
| amount | Valor doado |
| donor | Usuário doador |
| campaign | Campanha relacionada |
| paymentStatus | Status do pagamento |
| createdAt | Data de criação da doação |
