package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        Session session = factory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> from = query.from(Phone.class);
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String[]> map : params.entrySet()) {
            CriteriaBuilder.In<String> predicate = cb.in(from.get(map.getKey()));
            for (String s : map.getValue()) {
                predicate.value(s);
            }
            predicates.add(predicate);
        }
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return session.createQuery(query).getResultList();
    }

}
