//package br.ufsm.csi.pilacoin.repository;
//
//import br.ufsm.csi.pilacoin.model.PilaCoin;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Collection;
//import java.util.Optional;
//
//public interface PilaCoinRepository extends JpaRepository<PilaCoin, Long> {
//    Optional<PilaCoin> findPilaCoinByNonce(String nonce);
//    Collection<PilaCoin> findTop200StatusOrderByDataCriacao(PilaCoin.StatusPila statusPila);
//    Collection<PilaCoin> findTop200ByStatus(PilaCoin.StatusPila statusPila);
//}
