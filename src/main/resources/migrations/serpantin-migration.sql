-- Adding new report's categories
INSERT INTO public.category (name_key, description_key, deleted)
SELECT 'common.category.on.storage', 'common.category.on.storage.description', false
WHERE NOT EXISTS (SELECT 1 FROM public.category WHERE name_key = 'common.category.on.storage');

INSERT INTO public.category (name_key, description_key, deleted)
SELECT 'common.category.on.order', 'common.category.on.order.description', false
WHERE NOT EXISTS (SELECT 1 FROM public.category WHERE name_key = 'common.category.on.order');

INSERT INTO public.category (name_key, description_key, deleted)
SELECT 'common.category.on.office', 'common.category.on.office.description', false
WHERE NOT EXISTS (SELECT 1 FROM public.category WHERE name_key = 'common.category.on.office');

INSERT INTO public.category (name_key, description_key, deleted)
SELECT 'common.category.on.coordination', 'common.category.on.coordination.description', false
WHERE NOT EXISTS (SELECT 1 FROM public.category WHERE name_key = 'common.category.on.coordination');

-- Adding settings for scheduling
INSERT INTO public.setting (key, value)
SELECT 'scheduling.notification.start.report.reminder', '1.03'
    WHERE NOT EXISTS (SELECT 1 FROM public.setting WHERE key = 'scheduling.notification.start.report.reminder');

INSERT INTO public.setting (key, value)
SELECT 'scheduling.notification.finish.report.reminder', '1.11'
    WHERE NOT EXISTS (SELECT 1 FROM public.setting WHERE key = 'scheduling.notification.finish.report.reminder');

INSERT INTO public.setting (key, value)
SELECT 'scheduling.general.lock.edit.report.offset.month', '2'
    WHERE NOT EXISTS (SELECT 1 FROM public.setting WHERE key = 'scheduling.general.lock.edit.report.offset.month');

INSERT INTO public.setting (key, value)
SELECT 'scheduling.general.remove.unauthorized.user.offset.days', '20'
    WHERE NOT EXISTS (SELECT 1 FROM public.setting WHERE key = 'scheduling.general.remove.unauthorized.user.offset.days');

INSERT INTO public.setting (key, value)
SELECT 'scheduling.notification.exclude.employee.chat.ids', '666582175,831351389'
    WHERE NOT EXISTS (SELECT 1 FROM public.setting WHERE key = 'scheduling.notification.exclude.employee.chat.ids');

