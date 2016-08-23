package org.hqu.indoor_pos.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hqu.indoor_pos.bean.BaseStation;
import org.hqu.indoor_pos.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

public class BaseStationManageImpl extends UnicastRemoteObject implements BaseStationManage{

	private static final long serialVersionUID = 1L;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public BaseStationManageImpl() throws RemoteException {
		super();
	}
	
	/**
	 * 查找所有基站
	 */
	@Override
	public List<BaseStation> findAllBaseStation() throws RemoteException {

		return this.jdbcTemplate.query("select * from base_station",   
                new RowMapper<BaseStation>(){  
              
                    @Override  
                    public BaseStation mapRow(ResultSet rs, int rowNum) throws SQLException {  
                    	BaseStation base = new BaseStation(rs.getString(1),rs.getInt(2),rs.getDouble(3),rs.getDouble(4));
                        return base;  
                    }  
        });  
	}
	
	/**
	 * 根据房间id查找房间内的基站
	 * @param roomId
	 */
	@Override
	public List<BaseStation> findBaseStationByRoomId(Integer roomId) throws RemoteException {
		
		return this.jdbcTemplate.query("select * from base_station where room_id = ?",
				new Object[]{roomId},   
                new int[]{java.sql.Types.INTEGER},
                new RowMapper<BaseStation>(){  
              
                    @Override  
                    public BaseStation mapRow(ResultSet rs, int rowNum) throws SQLException {  
                    	BaseStation base = new BaseStation(rs.getString(1),rs.getInt(2),rs.getDouble(3),rs.getDouble(4));
                        return base;  
                    }  
        });  
	}

	/**
	 * 保存基站
	 * @param baseStation
	 */
	@Override
	public boolean saveBaseStation(BaseStation baseStation) throws RemoteException {
        
		try {
			this.jdbcTemplate.update("insert into base_station values (?, ?, ?, ?)",   
	                new Object[]{baseStation.getBaseId(), baseStation.getRoomId(), baseStation.getxAxis(), baseStation.getyAxis()});  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Server.roomIds.put(baseStation.getBaseId(), baseStation.getRoomId());
		Server.baseStationLocs.put(baseStation.getBaseId(), new Double[]{baseStation.getxAxis(), baseStation.getyAxis()});
		return true;
	}

	/**
	 * 修改基站信息
	 * @param baseStation
	 */
	@Override
	public boolean updateBaseStation(final BaseStation baseStation)
			throws RemoteException {
		
		try {
			this.jdbcTemplate.update(  
					"update base_station set room_id = ?, x_axis = ?, y_axis = ? where base_id = ?",   
	                new PreparedStatementSetter(){  
	                    @Override  
	                    public void setValues(PreparedStatement ps) throws SQLException {  
	                        ps.setInt(1, baseStation.getRoomId());  
	                        ps.setDouble(2, baseStation.getxAxis());
	                        ps.setDouble(3, baseStation.getyAxis());
	                        ps.setString(4, baseStation.getBaseId());
	                    }  
	                }  
	        );  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Server.roomIds.put(baseStation.getBaseId(), baseStation.getRoomId());
		Server.baseStationLocs.put(baseStation.getBaseId(), new Double[]{baseStation.getxAxis(), baseStation.getyAxis()});
		return true;
	}

	/**
	 * 删除基站
	 * @param baseId
	 */
	@Override
	public boolean deleteBaseStation(final String baseId) throws RemoteException {
		
		try {
			this.jdbcTemplate.update(  
					"delete from base_station  where room_id = ?",   
	                new PreparedStatementSetter(){  
	                    @Override  
	                    public void setValues(PreparedStatement ps) throws SQLException {  
	                        ps.setString(1, baseId);  
	                    }  
	                }  
	        );  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Server.roomIds.remove(baseId);
		Server.baseStationLocs.remove(baseId);
		return true;
	}

	/**
	 * 根据基站id查询基站
	 * @param baseId
	 */
	@Override
	public BaseStation getBaseStationById(String baseId) throws RemoteException {
		
		return (BaseStation) this.jdbcTemplate.queryForObject(  
                "select * from base_station where base_id = ?",   
                new Object[]{baseId},  
                new RowMapper<BaseStation>(){  
  
                    @Override  
                    public BaseStation mapRow(ResultSet rs,int rowNum)throws SQLException {  
                    	BaseStation baseStation  = new BaseStation(rs.getString(1),rs.getInt(2),rs.getDouble(3),rs.getDouble(4));  
                        return baseStation;  
                    }  
        }); 
	}
	
}