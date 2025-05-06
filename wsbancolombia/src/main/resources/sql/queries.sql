CREATE OR REPLACE PACKAGE SIA_BUSCAR_FACTURAS AS
    FUNCTION ORDEN_MATRICULA_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION CUOTA_CREDITO_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION OTRAS_DEUDAS_CARTERA_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION OTROS_ABONOS(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    --FUNCTION FACTURAS_COMERCIALES(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION TODAS_POR_RC(MY_REF NUMBER) RETURN SIA_BILL_DATA_TYPE;
    FUNCTION ORDEN_MATRICULA_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION CUOTA_CREDITO_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION OTRAS_DEUDAS_CARTERA_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR;
    --FUNCTION FACTURAS_COMERCIALES(MY_REF NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION TODAS_POR_NIT(NIT NUMBER) RETURN SIA_BILL_ARRAY;
END SIA_BUSCAR_FACTURAS;
/

CREATE OR REPLACE PACKAGE BODY SIA_BUSCAR_FACTURAS AS
  FUNCTION ORDEN_MATRICULA_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
    OPEN MCURSOR FOR select cliente, fecha_vencimiento, recibo_consignacion, referencia_academico,
            case when porcentaje < 0 then (valor_detalle_extra + valor_sin_extra)*(1 + (porcentaje/100))
                 else valor_detalle_extra*(1 + (porcentaje/100)) + valor_sin_extra end as valor_total
        from (
            select rec.cliente cliente, ven.fecha_vencimiento fecha_vencimiento, ven.porcentaje,
            rec.recibo_consignacion recibo_consignacion, cct.referencia_academico referencia_academico,
            (
                SELECT SUM( DRC.valor ) valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE   rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     (NVL(ts.incluye_liquidacion_extra, 'N') = 'S' OR drc.orden > 0)
                AND NOT EXISTS (
                    SELECT 'X' FROM concepto_ingreso_credito
                    WHERE concepto IN ('EX','CX')
                    AND concepto_nota = drc.concepto_nota
                    AND causa_nota = drc.causa_nota)
            ) valor_detalle_extra,
            (
                SELECT nvl(SUM     (DRC.valor),0)   valor
                FROM
                detalle_recibo_consignacion DRC,
                recibo_consignacion RC,
                tipo_solicitud_nota ts
                WHERE  rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     NVL(ts.incluye_liquidacion_extra, 'N') = 'N'
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     drc.orden IS NULL
                AND NOT EXISTS (SELECT 'X'
                FROM concepto_ingreso_credito
                WHERE concepto IN ( 'EX','CX')
                AND concepto_nota = drc.concepto_nota
                AND causa_nota = drc.causa_nota)
            ) valor_sin_extra
            from
            orden ord,
            recibo_consignacion rec,
            vencimiento_periodo ven,
            detalle_recibo_consignacion detcon,
            cct_ordenes_academico cct
            where
            ord.cliente_solicitado = rec.cliente
            and ord.grupo = ven.grupo
            and ord.orden = detcon.orden
            and ord.documento = detcon.documento_orden
            and ord.estado = 'V'
            and rec.estado = 'I'
            and ord.periodo = rec.periodo
            and ord.periodo = ven.periodo
            and ven.periodo = rec.periodo
            and rec.recibo_consignacion = cct.recibo_consignacion
            and rec.cliente = cct.cliente
            and ven.fecha_vencimiento >= TRUNC(SYSDATE)
            and rec.recibo_consignacion = detcon.recibo_consignacion
            and not exists(
                SELECT 'X' FROM tet_pagos_online WHERE recibo_consignacion = rec.recibo_consignacion
            ) and not exists (
                select 	liq.documento,
                liq.orden
                from liquidacion_orden liq
                where liq.estado = 'V'
                and liq.documento_originado = ord.documento
                and liq.orden = ord.orden
                and liq.organizacion_originado = ord.organizacion
            ) and rec.recibo_consignacion= MY_REF
            order by cliente, recibo_consignacion, fecha_vencimiento);
      RETURN MCURSOR;
  END ORDEN_MATRICULA_POR_RC;

  FUNCTION CUOTA_CREDITO_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
    OPEN MCURSOR FOR select
              r.cliente as cliente,
              r.fecha_pago as fecha_vencimiento,
              r.recibo_consignacion as  recibo_consignacion,
              sum(d.valor) as valor_total
        from recibo_consignacion r
        inner join detalle_recibo_consignacion d
        on d.recibo_consignacion = r.recibo_consignacion
        where not exists (
            select distinct rc.recibo_consignacion from recibo_consignacion rc
            inner join detalle_recibo_consignacion drc
            on drc.recibo_consignacion = rc.recibo_consignacion
            where  rc.estado= 'I'
            and drc.orden > 0
            and rc.recibo_consignacion = r.recibo_consignacion
        )
        and not exists (
            select distinct rc.recibo_consignacion from recibo_consignacion rc
            inner join detalle_recibo_consignacion drc
            on drc.recibo_consignacion = rc.recibo_consignacion
            where  rc.estado= 'I'
            and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
            and rc.recibo_consignacion = r.recibo_consignacion
        )
        and not exists(
            SELECT 'X'
            FROM tet_pagos_online
            WHERE recibo_consignacion = r.recibo_consignacion
         )
        and exists (select 'x' from credito cr
            inner join nota_detalle_credito ndc on
            ndc.credito = cr.credito
            inner join detalle_recibo_consignacion drc
            on to_number(trim(replace(substr(drc.observaciones, instr(drc.observaciones,':')+1,instr(drc.observaciones,' ',instr(drc.observaciones,':'),2)-instr(drc.observaciones,':')),'.','')))= cr.credito
            and  to_number(trim(substr(drc.observaciones, instr(drc.observaciones,':',1,2)+1,2))) = ndc.cuota
            inner join saldo_cartera sc
            on sc.documento = 'NDB'
            and sc.numero_credito = ndc.nota_debito
            and sc.organizacion = ndc.organizacion
            where cr.estado in ('C','E')
            and ndc.concepto = 'C'
            and drc.recibo_consignacion = r.recibo_consignacion
            and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
            and upper(drc.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
            and (sc.valor_documento - sc.valor_afectado) > 100
        )
        and 0 >= (select nvl(sum(valor_documento - valor_afectado), 0) from saldo_cartera
                          where cliente = r.cliente and trunc(fecha_vencimiento) < trunc(r.fecha_pago))
        and r.estado = 'I'
        and r.fecha_pago >= trunc(sysdate)
        and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
        and (r.recibo_consignacion = MY_REF)
        group by
              r.cliente,
              r.fecha_pago,
              r.recibo_consignacion
        order by r.fecha_pago, r.recibo_consignacion;
    RETURN MCURSOR;
  END CUOTA_CREDITO_POR_RC;

  FUNCTION OTRAS_DEUDAS_CARTERA_POR_RC(MY_REF NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
    OPEN MCURSOR FOR select  r.cliente as cliente, r.fecha_pago as fecha_vencimiento,
            r.recibo_consignacion as  recibo_consignacion, sum(d.valor) as valor_total
        from recibo_consignacion r
        inner join detalle_recibo_consignacion d
        on d.recibo_consignacion = r.recibo_consignacion
        where not exists (
            select distinct rc.recibo_consignacion from recibo_consignacion rc
            inner join detalle_recibo_consignacion drc
            on drc.recibo_consignacion = rc.recibo_consignacion
            where  rc.estado= 'I'
            and drc.orden > 0
            and rc.recibo_consignacion = r.recibo_consignacion)
        and not exists (
            select distinct rc.recibo_consignacion from recibo_consignacion rc
            inner join detalle_recibo_consignacion drc
            on drc.recibo_consignacion = rc.recibo_consignacion
            where  rc.estado= 'I'
            and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
            and rc.recibo_consignacion = r.recibo_consignacion)
        and not exists(
            SELECT 'X'
            FROM tet_pagos_online
            WHERE recibo_consignacion = r.recibo_consignacion)
        and r.estado = 'I'
        and r.fecha_pago >= trunc(sysdate)
        and r.observaciones not like '%NOTA GENERADA PARA EL CREDITO%'
        and (r.recibo_consignacion = MY_REF)
        group by r.cliente, r.fecha_pago, r.recibo_consignacion
        order by r.fecha_pago, r.recibo_consignacion;
    RETURN MCURSOR;
  END OTRAS_DEUDAS_CARTERA_POR_RC;

  FUNCTION OTROS_ABONOS(MY_REF NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
    OPEN MCURSOR FOR Select recibo.cliente cliente, recibo.fecha_pago fecha_vencimiento,
            recibo.recibo_consignacion recibo_consignacion, recibo.valor valor_total
        From recibo_consignacion recibo
        inner join cliente c on c.cliente = recibo.cliente
        inner join(
            Select recibo_consignacion, concepto_nota, causa_nota,
            nombre_concepto, nombre_causa_nota, concepto
            From (
                Select
                drc.recibo_consignacion,
                Row_Number() OVER (PARTITION BY drc.recibo_consignacion,  drc.concepto_nota, drc.causa_nota ORDER BY drc.recibo_consignacion, drc.secuencia) item,
                drc.concepto_nota, drc.causa_nota,
                co.nombre_concepto, ca.nombre_causa_nota,
                'PAGO CONCEPTO ' || drc.concepto_nota || '-' ||drc.causa_nota || ' ' ||  co.nombre_concepto || '/' || ca.nombre_causa_nota concepto
                From
                recibo_consignacion rc,
                detalle_recibo_consignacion drc,
                concepto_nota co,
                causa_nota ca
                Where
                rc.recibo_consignacion = drc.recibo_consignacion
                And drc.concepto_nota=co.concepto_nota
                And drc.causa_nota = ca.causa_nota
                And NVL(rc.observaciones,'NULL') NOT LIKE 'NOTA GENERADA PARA EL CREDITO:%%EN LA CUOTA:%%'
                And drc.documento IS NULL
                And drc.numero_credito IS NULL
                And drc.documento_orden IS NULL
                And drc.orden IS NULL
            )
            Where item = 1
         ) concepto on recibo.recibo_consignacion = concepto.recibo_consignacion
        Where TRUNC(recibo.fecha_pago) >=  TRUNC(SYSDATE)
        And recibo.estado = 'I'
        and not exists(
            SELECT 'X' FROM tet_pagos_online WHERE recibo_consignacion = recibo.recibo_consignacion
        )
        and recibo.recibo_consignacion = MY_REF order by recibo.fecha_pago;
    RETURN MCURSOR;
  END OTROS_ABONOS;

  FUNCTION TODAS_POR_RC(MY_REF NUMBER) RETURN SIA_BILL_DATA_TYPE IS
    CLIENTE NUMBER(16,0);
    FECHA_VENCIMIENTO DATE;
    RECIBO_CONSIGNACION NUMBER;
    REFERENCIA_ACADEMICO VARCHAR2(20);
    VALOR_TOTAL NUMBER;
    TIPO_RECIBO VARCHAR2(10);
    MY_BILL SIA_BILL_DATA_TYPE;
    C1 SYS_REFCURSOR;
  BEGIN
    C1 := ORDEN_MATRICULA_POR_RC(MY_REF);
    FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, REFERENCIA_ACADEMICO, VALOR_TOTAL;

    IF C1%NOTFOUND THEN
      CLOSE C1;
      C1 := CUOTA_CREDITO_POR_RC(MY_REF);
      FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, VALOR_TOTAL;
      IF C1%FOUND THEN
        REFERENCIA_ACADEMICO := 'CDT';
        TIPO_RECIBO := 'CREDITO';
      ELSE
        CLOSE C1;
        C1 := OTRAS_DEUDAS_CARTERA_POR_RC(MY_REF);
        FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, VALOR_TOTAL;
        IF C1%FOUND THEN
            REFERENCIA_ACADEMICO := 'OTR';
            TIPO_RECIBO := 'CONCEPTO';
        ELSE
            CLOSE C1;
            C1 := OTROS_ABONOS(MY_REF);
            FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, VALOR_TOTAL;
            IF C1%FOUND THEN
                REFERENCIA_ACADEMICO := 'OTR';
                TIPO_RECIBO := 'ABONO';
            END IF;
        END IF;
      END IF;
    ELSE
      TIPO_RECIBO := 'ORDEN';
    END IF;

    CLOSE C1;

    IF CLIENTE IS NOT NULL THEN
      MY_BILL := SIA_BILL_DATA_TYPE(CLIENTE, VALOR_TOTAL, FECHA_VENCIMIENTO,
                    RECIBO_CONSIGNACION, REFERENCIA_ACADEMICO, TIPO_RECIBO);
    END IF;
    RETURN MY_BILL;
  END TODAS_POR_RC;

  FUNCTION ORDEN_MATRICULA_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR IS
      MCURSOR SYS_REFCURSOR;
    BEGIN
      OPEN MCURSOR FOR select cliente, fecha_vencimiento, recibo_consignacion, referencia_academico,
          case when porcentaje < 0 then (valor_detalle_extra + valor_sin_extra)*(1 + (porcentaje/100))
               else valor_detalle_extra*(1 + (porcentaje/100)) + valor_sin_extra end as valor_total
        from ( select rec.cliente cliente, ven.fecha_vencimiento fecha_vencimiento, ven.porcentaje porcentaje,
            rec.recibo_consignacion recibo_consignacion, cct.referencia_academico referencia_academico,
            (
                SELECT SUM     ( DRC.valor )   valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE   rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     (NVL(ts.incluye_liquidacion_extra, 'N') = 'S' OR drc.orden > 0)
                AND NOT EXISTS (
                    SELECT 'X' FROM concepto_ingreso_credito
                    WHERE concepto IN ('EX','CX')
                    AND concepto_nota = drc.concepto_nota
                    AND causa_nota = drc.causa_nota)
            ) valor_detalle_extra,
            (
                SELECT nvl(SUM     (DRC.valor),0)   valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE  rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     NVL(ts.incluye_liquidacion_extra, 'N') = 'N'
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     drc.orden IS NULL
                AND NOT EXISTS (SELECT 'X' FROM concepto_ingreso_credito
                    WHERE concepto IN ( 'EX','CX')
                    AND concepto_nota = drc.concepto_nota
                    AND causa_nota = drc.causa_nota)
            )  valor_sin_extra
            from
            orden ord, recibo_consignacion rec, vencimiento_periodo ven,
            detalle_recibo_consignacion detcon, cliente cli, cct_ordenes_academico cct
            where ord.cliente_solicitado = rec.cliente
            and rec.cliente  = cli.cliente
            and ord.grupo = ven.grupo
            and ord.orden = detcon.orden
            and ord.documento = detcon.documento_orden
            and ord.estado = 'V'
            and rec.estado = 'I'
            and ord.periodo = rec.periodo
            and ord.periodo = ven.periodo
            and ven.periodo = rec.periodo
            and rec.recibo_consignacion = cct.recibo_consignacion
            and rec.cliente = cct.cliente
            and ven.fecha_vencimiento >= TRUNC(SYSDATE)
            and rec.recibo_consignacion = detcon.recibo_consignacion
            and not exists(
                SELECT 'X' FROM tet_pagos_online WHERE recibo_consignacion = rec.recibo_consignacion
            )
            and not exists (
                select 	liq.documento, liq.orden
                from liquidacion_orden liq
                where liq.estado = 'V'
                and liq.documento_originado = ord.documento
                and liq.orden = ord.orden
                and liq.organizacion_originado = ord.organizacion
            )
            and (cli.cliente = NIT)
        ) order by cliente, recibo_consignacion, fecha_vencimiento;
      RETURN MCURSOR;
  END ORDEN_MATRICULA_POR_NIT;

  FUNCTION CUOTA_CREDITO_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
    OPEN MCURSOR FOR select  r.cliente as cliente,
            r.fecha_pago as fecha_vencimiento,
            r.recibo_consignacion as  recibo_consignacion,
            sum(d.valor) as valor_total
    from recibo_consignacion r
    inner join detalle_recibo_consignacion d
    on d.recibo_consignacion = r.recibo_consignacion
    where not exists (
    select distinct rc.recibo_consignacion from recibo_consignacion rc
    inner join detalle_recibo_consignacion drc
    on drc.recibo_consignacion = rc.recibo_consignacion
    where  rc.estado= 'I'
    and drc.orden > 0
    and rc.recibo_consignacion = r.recibo_consignacion
    )
    and not exists (
       select distinct rc.recibo_consignacion from recibo_consignacion rc
    inner join detalle_recibo_consignacion drc
    on drc.recibo_consignacion = rc.recibo_consignacion
    where  rc.estado= 'I'
    and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
    and rc.recibo_consignacion = r.recibo_consignacion
    )
    and not exists(
            SELECT 'X'
            FROM tet_pagos_online
            WHERE recibo_consignacion = r.recibo_consignacion
              )
         and exists (select 'x' from credito cr
                        inner join nota_detalle_credito ndc on
                        ndc.credito = cr.credito
                        inner join detalle_recibo_consignacion drc
                        on to_number(trim(replace(substr(drc.observaciones, instr(drc.observaciones,':')+1,instr(drc.observaciones,' ',instr(drc.observaciones,':'),2)-instr(drc.observaciones,':')),'.','')))= cr.credito
                        and  to_number(trim(substr(drc.observaciones, instr(drc.observaciones,':',1,2)+1,2))) = ndc.cuota
                        inner join saldo_cartera sc
                        on sc.documento = 'NDB'
                        and sc.numero_credito = ndc.nota_debito
                        and sc.organizacion = ndc.organizacion
                        where cr.estado in ('C','E')
                        and ndc.concepto = 'C'
                        and drc.recibo_consignacion = r.recibo_consignacion
                        and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
                        and upper(drc.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
                        and (sc.valor_documento - sc.valor_afectado) > 100
                        )
    and r.estado = 'I'
    and r.fecha_pago >= trunc(sysdate)
    and 0 >= (select nvl(sum(valor_documento - valor_afectado), 0) from saldo_cartera
              where cliente = r.cliente and trunc(fecha_vencimiento) < trunc(r.fecha_pago))
    and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
    and (r.cliente = NIT)
    group by
    r.cliente,
    r.fecha_pago,
    r.recibo_consignacion
    order by r.fecha_pago, r.recibo_consignacion;

    RETURN MCURSOR;
  END CUOTA_CREDITO_POR_NIT;

  FUNCTION OTRAS_DEUDAS_CARTERA_POR_NIT(NIT NUMBER) RETURN SYS_REFCURSOR IS
    MCURSOR SYS_REFCURSOR;
  BEGIN
      OPEN MCURSOR FOR select r.cliente as cliente, r.fecha_pago as fecha_vencimiento,
            r.recibo_consignacion as  recibo_consignacion, sum(d.valor) as valor_total
          from recibo_consignacion r
          inner join detalle_recibo_consignacion d
          on d.recibo_consignacion = r.recibo_consignacion
              where not exists (
              select distinct rc.recibo_consignacion from recibo_consignacion rc
              inner join detalle_recibo_consignacion drc
              on drc.recibo_consignacion = rc.recibo_consignacion
              where  rc.estado= 'I'
              and drc.orden > 0
              and rc.recibo_consignacion = r.recibo_consignacion)
          and not exists (
              select distinct rc.recibo_consignacion from recibo_consignacion rc
              inner join detalle_recibo_consignacion drc
              on drc.recibo_consignacion = rc.recibo_consignacion
              where  rc.estado= 'I'
              and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
              and rc.recibo_consignacion = r.recibo_consignacion)
          and not exists(
              SELECT 'X'
              FROM tet_pagos_online
              WHERE recibo_consignacion = r.recibo_consignacion)
          and r.estado = 'I'
          and r.fecha_pago >= trunc(sysdate)
          and r.observaciones not like '%NOTA GENERADA PARA EL CREDITO%'
          and (r.cliente = NIT)
          group by r.cliente, r.fecha_pago, r.recibo_consignacion
          order by r.fecha_pago, r.recibo_consignacion;

      RETURN MCURSOR;
  END OTRAS_DEUDAS_CARTERA_POR_NIT;

  FUNCTION TODAS_POR_NIT(NIT NUMBER) RETURN SIA_BILL_ARRAY IS
    BILL_LIST SIA_BILL_ARRAY;
  BEGIN
    select SIA_BILL_DATA_TYPE(cliente, valor_total, fecha_vencimiento,
        recibo_consignacion, referencia_academico, tipo_recibo)
        BULK COLLECT INTO BILL_LIST from
            (select cliente, case when porcentaje < 0 then (valor_detalle_extra + valor_sin_extra)*(1 + (porcentaje/100))
                else valor_detalle_extra*(1 + (porcentaje/100)) + valor_sin_extra end as valor_total,
                fecha_vencimiento, recibo_consignacion, referencia_academico, 'ORDEN' tipo_recibo
            from ( select rec.cliente cliente, ven.fecha_vencimiento fecha_vencimiento, ven.porcentaje porcentaje,
                rec.recibo_consignacion recibo_consignacion, cct.referencia_academico referencia_academico,
                (
                SELECT SUM     ( DRC.valor )   valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE   rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     (NVL(ts.incluye_liquidacion_extra, 'N') = 'S' OR drc.orden > 0)
                AND NOT EXISTS (
                SELECT 'X' FROM concepto_ingreso_credito
                WHERE concepto IN ('EX','CX')
                AND concepto_nota = drc.concepto_nota
                AND causa_nota = drc.causa_nota)
                ) valor_detalle_extra,
                (
                SELECT nvl(SUM     (DRC.valor),0)   valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE  rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     NVL(ts.incluye_liquidacion_extra, 'N') = 'N'
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     drc.orden IS NULL
                AND NOT EXISTS (SELECT 'X' FROM concepto_ingreso_credito
                WHERE concepto IN ( 'EX','CX')
                AND concepto_nota = drc.concepto_nota
                AND causa_nota = drc.causa_nota)
                )  valor_sin_extra
                from
                orden ord, recibo_consignacion rec, vencimiento_periodo ven,
                detalle_recibo_consignacion detcon, cliente cli, cct_ordenes_academico cct
                where ord.cliente_solicitado = rec.cliente
                and rec.cliente  = cli.cliente
                and ord.grupo = ven.grupo
                and ord.orden = detcon.orden
                and ord.documento = detcon.documento_orden
                and ord.estado = 'V'
                and rec.estado = 'I'
                and ord.periodo = rec.periodo
                and ord.periodo = ven.periodo
                and ven.periodo = rec.periodo
                and rec.recibo_consignacion = cct.recibo_consignacion
                and rec.cliente = cct.cliente
                and ven.fecha_vencimiento >= TRUNC(SYSDATE)
                and rec.recibo_consignacion = detcon.recibo_consignacion
                and not exists(
                    SELECT 'X' FROM tet_pagos_online WHERE recibo_consignacion = rec.recibo_consignacion
                )
                and not exists (
                    select 	liq.documento, liq.orden
                    from liquidacion_orden liq
                    where liq.estado = 'V'
                    and liq.documento_originado = ord.documento
                    and liq.orden = ord.orden
                    and liq.organizacion_originado = ord.organizacion
                )
                and (cli.cliente = NIT)
            )
            union all
            select  r.cliente as cliente, sum(d.valor) as valor_total, r.fecha_pago as fecha_vencimiento,
                r.recibo_consignacion as  recibo_consignacion, 'CDT' as referencia_academico, 'CREDITO' as tipo_recibo
            from recibo_consignacion r
            inner join detalle_recibo_consignacion d
                on d.recibo_consignacion = r.recibo_consignacion
            where not exists (
                select distinct rc.recibo_consignacion from recibo_consignacion rc
                inner join detalle_recibo_consignacion drc
                on drc.recibo_consignacion = rc.recibo_consignacion
                where  rc.estado= 'I'
                and drc.orden > 0
                and rc.recibo_consignacion = r.recibo_consignacion)
            and not exists (
                select distinct rc.recibo_consignacion from recibo_consignacion rc
                inner join detalle_recibo_consignacion drc
                on drc.recibo_consignacion = rc.recibo_consignacion
                where  rc.estado= 'I'
                and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
                and rc.recibo_consignacion = r.recibo_consignacion)
            and not exists(
                SELECT 'X'
                FROM tet_pagos_online
                WHERE recibo_consignacion = r.recibo_consignacion)
            and exists (select 'x' from credito cr
                inner join nota_detalle_credito ndc on
                ndc.credito = cr.credito
                inner join detalle_recibo_consignacion drc
                on to_number(trim(replace(substr(drc.observaciones, instr(drc.observaciones,':')+1,instr(drc.observaciones,' ',instr(drc.observaciones,':'),2)-instr(drc.observaciones,':')),'.','')))= cr.credito
                and  to_number(trim(substr(drc.observaciones, instr(drc.observaciones,':',1,2)+1,2))) = ndc.cuota
                inner join saldo_cartera sc
                on sc.documento = 'NDB'
                and sc.numero_credito = ndc.nota_debito
                and sc.organizacion = ndc.organizacion
                where cr.estado in ('C','E')
                and ndc.concepto = 'C'
                and drc.recibo_consignacion = r.recibo_consignacion
                and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
                and upper(drc.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
                and (sc.valor_documento - sc.valor_afectado) > 100)
            and r.estado = 'I'
            and r.fecha_pago >= trunc(sysdate)
            and 0 >= (select nvl(sum(valor_documento - valor_afectado), 0) from saldo_cartera
            where cliente = r.cliente and trunc(fecha_vencimiento) < trunc(r.fecha_pago))
            and upper(r.observaciones) like '%NOTA GENERADA PARA EL CREDITO%'
            and (r.cliente = NIT)
            group by r.cliente, r.fecha_pago, r.recibo_consignacion
            union all
            select r.cliente as cliente, sum(d.valor) as valor_total, r.fecha_pago as fecha_vencimiento,
                r.recibo_consignacion as recibo_consignacion, 'OTR' as referencia_academico, 'CONCEPTO' as tipo_recibo
            from recibo_consignacion r
            inner join detalle_recibo_consignacion d
            on d.recibo_consignacion = r.recibo_consignacion
              where not exists (
              select distinct rc.recibo_consignacion from recibo_consignacion rc
              inner join detalle_recibo_consignacion drc
              on drc.recibo_consignacion = rc.recibo_consignacion
              where  rc.estado= 'I'
              and drc.orden > 0
              and rc.recibo_consignacion = r.recibo_consignacion)
            and not exists (
              select distinct rc.recibo_consignacion from recibo_consignacion rc
              inner join detalle_recibo_consignacion drc
              on drc.recibo_consignacion = rc.recibo_consignacion
              where  rc.estado= 'I'
              and drc.documento in (select distinct documento from documento_cartera where tipo_documento = 'F' )
              and rc.recibo_consignacion = r.recibo_consignacion)
            and not exists(
              SELECT 'X'
              FROM tet_pagos_online
              WHERE recibo_consignacion = r.recibo_consignacion)
            and r.estado = 'I'
            and r.fecha_pago >= trunc(sysdate)
            and r.observaciones not like '%NOTA GENERADA PARA EL CREDITO%'
            and (r.cliente = NIT)
            group by r.cliente, r.fecha_pago, r.recibo_consignacion
        ) order by cliente, recibo_consignacion, fecha_vencimiento;
        RETURN BILL_LIST;
  END TODAS_POR_NIT;
END SIA_BUSCAR_FACTURAS;
/
