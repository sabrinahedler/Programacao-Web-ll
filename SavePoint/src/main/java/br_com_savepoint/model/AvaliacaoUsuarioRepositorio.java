package br_com_savepoint.model;

import br.com.savepoint.savepoint.model.AvaliacaoUsuario;
import br.com.savepoint.savepoint.model.Jogo;
import br.com.savepoint.savepoint.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AvaliacaoUsuarioRepositorio {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private JogoRepositorio jogoRepositorio;

    // RowMapper como classe interna
    private class AvaliacaoUsuarioRowMapper implements RowMapper<AvaliacaoUsuario> {
        @Override
        public AvaliacaoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            AvaliacaoUsuario avaliacao = new AvaliacaoUsuario();
            avaliacao.setId(rs.getLong("id"));
            avaliacao.setNota(rs.getInt("nota"));
            avaliacao.setTextoAvaliacao(rs.getString("texto_avaliacao"));
            
            Timestamp dataPublicacao = rs.getTimestamp("data_publicacao");
            if (dataPublicacao != null) {
                avaliacao.setDataPublicacao(dataPublicacao.toLocalDateTime());
            }
            
            avaliacao.setCurtidas(rs.getInt("curtidas"));
            avaliacao.setHorasJogadas(rs.getInt("horas_jogadas"));
            avaliacao.setUtilVoto(rs.getInt("util_voto"));
            
            Long usuarioId = rs.getLong("usuario_id");
            if (usuarioId > 0) {
                Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId);
                avaliacao.setUsuario(usuario);
            }
            
            Long jogoId = rs.getLong("jogo_id");
            if (jogoId > 0) {
                Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
                avaliacao.setJogo(jogo);
            }
            
            return avaliacao;
        }
    }

    //  CRUD
    
    public List<AvaliacaoUsuario> listarTodos() {
        String sql = "SELECT * FROM avaliacoes_usuario ORDER BY id";
        return jdbcTemplate.query(sql, new AvaliacaoUsuarioRowMapper());
    }

    public AvaliacaoUsuario buscarPorId(Long id) {
        String sql = "SELECT * FROM avaliacoes_usuario WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new AvaliacaoUsuarioRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<AvaliacaoUsuario> buscarPorJogoId(Long jogoId) {
        String sql = "SELECT * FROM avaliacoes_usuario WHERE jogo_id = ? ORDER BY data_publicacao DESC";
        try {
            return jdbcTemplate.query(sql, new AvaliacaoUsuarioRowMapper(), jogoId);
        } catch (Exception e) {
            return new ArrayList<AvaliacaoUsuario>();
        }
    }

    public List<AvaliacaoUsuario> buscarPorUsuarioId(Long usuarioId) {
        String sql = "SELECT * FROM avaliacoes_usuario WHERE usuario_id = ? ORDER BY data_publicacao DESC";
        try {
            return jdbcTemplate.query(sql, new AvaliacaoUsuarioRowMapper(), usuarioId);
        } catch (Exception e) {
            return new ArrayList<AvaliacaoUsuario>();
        }
    }

    public double calcularMediaJogo(Long jogoId) {
        String sql = "SELECT AVG(nota) FROM avaliacoes_usuario WHERE jogo_id = ?";
        try {
            Double media = jdbcTemplate.queryForObject(sql, Double.class, jogoId);
            if (media != null) {
                return media;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double calcularPercentualRecomendacao(Long jogoId) {
        String sql = "SELECT COUNT(*) FROM avaliacoes_usuario WHERE jogo_id = ? AND nota >= 4";
        String sqlTotal = "SELECT COUNT(*) FROM avaliacoes_usuario WHERE jogo_id = ?";
        
        try {
            Integer recomendam = jdbcTemplate.queryForObject(sql, Integer.class, jogoId);
            Integer total = jdbcTemplate.queryForObject(sqlTotal, Integer.class, jogoId);
            
            if (total != null && total > 0 && recomendam != null) {
                return (recomendam.doubleValue() / total.doubleValue()) * 100.0;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public AvaliacaoUsuario salvar(AvaliacaoUsuario avaliacao) {
        if (avaliacao.getId() == null) {
            return inserir(avaliacao);
        } else {
            return atualizar(avaliacao);
        }
    }

    private AvaliacaoUsuario inserir(AvaliacaoUsuario avaliacao) {
        String sql = "INSERT INTO avaliacoes_usuario " +
                    "(nota, texto_avaliacao, data_publicacao, curtidas, horas_jogadas, util_voto, usuario_id, jogo_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(new org.springframework.jdbc.core.PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, avaliacao.getNota());
                ps.setString(2, avaliacao.getTextoAvaliacao());
                ps.setTimestamp(3, Timestamp.valueOf(avaliacao.getDataPublicacao()));
                ps.setInt(4, avaliacao.getCurtidas());
                ps.setInt(5, avaliacao.getHorasJogadas());
     ps.setInt(6, avaliacao.getUtilVoto());
            ps.setLong(7, avaliacao.getUsuario().getId());
                ps.setLong(8, avaliacao.getJogo().getId());
                return ps;
            }
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            avaliacao.setId(keyHolder.getKey().longValue());
        }
        
        return avaliacao;
    }

    private AvaliacaoUsuario atualizar(AvaliacaoUsuario avaliacao) {
        String sql = "UPDATE avaliacoes_usuario SET " +
               "nota = ?, texto_avaliacao = ?, curtidas = ?, horas_jogadas = ?, util_voto = ? " +
                    "WHERE id = ?";
        
        jdbcTemplate.update(sql,
            avaliacao.getNota(),
            avaliacao.getTextoAvaliacao(),
            avaliacao.getCurtidas(),
            avaliacao.getHorasJogadas(),
            avaliacao.getUtilVoto(),
            avaliacao.getId()
        );
        
        return avaliacao;
    }

    public void deletarPorId(Long id) {
        String sql = "DELETE FROM avaliacoes_usuario WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deletarPorJogoId(Long jogoId) {
        String sql = "DELETE FROM avaliacoes_usuario WHERE jogo_id = ?";
        jdbcTemplate.update(sql, jogoId);
    }

    public void deletarPorUsuarioId(Long usuarioId) {
        String sql = "DELETE FROM avaliacoes_usuario WHERE usuario_id = ?";
        jdbcTemplate.update(sql, usuarioId);
    }

    public long contarPorJogoId(Long jogoId) { String sql = "SELECT COUNT(*) FROM avaliacoes_usuario WHERE jogo_id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, jogoId);
            if (count != null) {
                return count.longValue();
            }
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
