package com.zhonghao.utils;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class BaseDaoHibernate4<T> implements BaseDao<T> {

	// Dao 组件进行持久化操作底层依赖的 SessionFactory 组件
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	// 根据 ID 加载实体
	@SuppressWarnings("unchecked")
	public T get(Class<T> entityClazz, Serializable id) {
		return (T) getSessionFactory().getCurrentSession().get(entityClazz, id);
	}

	// 保存实体
	public Serializable save(T entity) {
		return getSessionFactory().getCurrentSession().save(entity);
	}

	// 更新实体
	public void update(T entity) {
		getSessionFactory().getCurrentSession().update(entity);
	}

	// 删除实体
	public void delete(T entity) {
		getSessionFactory().getCurrentSession().delete(entity);
	}

	// 根据 ID 删除实体
	public void delete(Class<T> entityClazz, Serializable id,String idName) {
		getSessionFactory().getCurrentSession()
			.createQuery("delete" + entityClazz.getSimpleName() + "en where en." + idName + "= ?0")
			.setParameter(0, id).executeUpdate();
	}

	@Override
	public List<T> findAll(Class<T> entityClazz) {
		
		return find("select en from " + entityClazz.getSimpleName() + " en");
	}

	@Override
	public long findCount(Class<T> entityClazz) {
		List<?> l = find("select count(*) from " + entityClazz.getSimpleName());
		// 返回查询结果得到的实体总数
		if(l != null && l.size() == 1) {
			return (long)l.get(0);
		}
		return 0;
	}

	// 根据 HQL 语句查询实体
	@SuppressWarnings("unchecked")
	protected List<T> find(String hql) {
		return (List<T>)getSessionFactory().getCurrentSession().createQuery(hql).list();
	}

	// 根据带占位符参数的 HQL 语句查询实体
	@SuppressWarnings("unchecked")
	protected List<T> find(String hql,Object... params) {
		 // 创建查询
		Query query = getSessionFactory().getCurrentSession().createQuery(hql);
		for(int i = 0,len = params.length;i < len;i++) {
			query.setParameter(i, params[i]);
		}
		return (List<T>)query.list();
	}

	// 使用 HQL 语句进行分页查询查找
	@SuppressWarnings("unchecked")
	protected List<T> findByPage(String hql,int pageNo,int pageSize) {
				// 创建查询
		return getSessionFactory().getCurrentSession().createQuery(hql)
				// 执行分页
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.list();
	}
	
	// 使用 HQL 语句进行分页查询操作
	@SuppressWarnings("unchecked")
	protected List<T> findByPage(String hql,int pageNo,int pageSize,Object... params) {
		// 创建查询
		Query query = getSessionFactory().getCurrentSession().createQuery(hql);
		// 为包含占位符的 HQL 语句设置参数
		for(int i = 0,len = params.length;i < len;i++){
			query.setParameter(i, params[i]);
		}
		// 执行分页，并返回结果
		return query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

}
