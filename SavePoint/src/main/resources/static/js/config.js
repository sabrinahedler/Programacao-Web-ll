// area dp admin

/*
 * imagem dos jogos:
 *
 * chave é o ID do jogo no banco
 * o site tenta o 1 link, se a imagem não abrir, tenta o 2, depois o 3
 * se nenhum abrir, mostra um ícone cinza com "Aguardando imagem".
 *
 * se um jogo NÃO estiver nesta lista, o site usa o campo "imagemCapa" do banco
 * se estiver com menos de 3 links, o "imagemCapa" entra como reserva no fim
 *
 * formato: imagem horizontal (proporção 460 x 215)
 */
const IMAGENS_JOGOS = {
  // Exemplo (troque pelos IDs e links reais):
  // 1: [
  //   "https://cdn.exemplo.com/elden-ring-capa.jpg",   // principal
  //   "https://outro-site.com/elden-ring.jpg",         // reserva 1
  //   "https://mais-um-site.com/elden-ring.png",       // reserva 2
  // ],
};

/*
 * fotos de perfil:
 *
 * arquivos na pasta  src/main/resources/static/assets/avatares/
 * nome do arquivo só pode ter letras, números, hífen e underline
 * extensão precisa ser .png, .jpg, .jpeg, .webp ou .svg
 * tamanho recomendado: 256 x 256 - quadrada
 */
const AVATARES = [
  // exemplo: { arquivo: "/assets/avatares/xxx.png", nome: "xxx" }
];
